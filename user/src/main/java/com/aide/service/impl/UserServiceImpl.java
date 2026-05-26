
package com.aide.service.impl;

import com.aide.adapter.VO.LoginResponse;
import com.aide.adapter.VO.RegisterRequest;
import com.aide.common.auth.context.UserContext;
import com.aide.common.auth.service.CacheService;
import com.aide.domain.service.UserDomainService;
import com.aide.domain.model.UserDo;
import com.aide.infrastructure.storage.FileStorageService;
import com.aide.infrastructure.persistence.entity.User;
import com.aide.infrastructure.persistence.mapper.UserMapper;
import com.aide.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserDomainService userDomainService;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;
    private final SecretKey jwtSecretKey;
    private final Long jwtExpiration;
    private final FileStorageService fileStorageService;

    @Value("${server.port:8081}")
    private String serverPort;

    public UserServiceImpl(UserDomainService userDomainService,
                           CacheService cacheService,
                           ObjectMapper objectMapper,
                           SecretKey jwtSecretKey,
                           @Value("${jwt.expiration:7200}") Long jwtExpiration,
                           FileStorageService fileStorageService) {
        this.userDomainService = userDomainService;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
        this.jwtSecretKey = jwtSecretKey;
        this.jwtExpiration = jwtExpiration;
        this.fileStorageService = fileStorageService;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(String account, String password, String loginIp) {
        // 应用服务只负责：

        // 1. 调用领域服务完成核心业务逻辑
        UserDo userDo = userDomainService.login(account, password, loginIp);

        // 2. 记录日志
        log.info("用户登录成功，用户ID: {}, 账号: {}", userDo.getId(), userDo.getAccount());

        // 3. 生成token
        String token = generateToken(userDo);

        // 4. 将用户信息保存到缓存（使用token作为key）
        saveUserToCache(token, userDo);

        // 5. 构建响应VO
        return LoginResponse.builder()
                .userId(userDo.getId())
                .account(userDo.getAccount())
                .username(userDo.getUsername())
                .token(generateToken(userDo))
                .role(userDo.getRole())
                .status(userDo.getStatus())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse register(RegisterRequest registerRequest, String loginIp) {
        // 使用 Builder 模式创建领域对象，而不是 BeanUtils.copyProperties
        UserDo userDo = UserDo.builder()
                .account(registerRequest.getAccount())
                .password(registerRequest.getPassword())
                .username(registerRequest.getUsername())
                .mobile(registerRequest.getMobile())
                .email(registerRequest.getEmail())
                .sex(registerRequest.getSex())
                .birthday(registerRequest.getBirthday())
                .occupation(registerRequest.getOccupation())
                .build();

        UserDo createdUser = userDomainService.createUser(userDo, loginIp);

        log.info("用户注册成功，用户ID: {}, 账号: {}", createdUser.getId(), createdUser.getAccount());

        // 生成token
        String token = generateToken(createdUser);

        // 将用户信息保存到缓存
        saveUserToCache(token, createdUser);

        return LoginResponse.builder()
                .userId(createdUser.getId())
                .account(createdUser.getAccount())
                .username(createdUser.getUsername())
                .token(generateToken(createdUser))
                .role(createdUser.getRole())
                .status(createdUser.getStatus())
                .build();
    }

    /**
     * 生成JWT令牌（简化版本，实际项目中需要使用JWT库）
     */
    private String generateToken(UserDo userDo) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

        return Jwts.builder()
                .setSubject(String.valueOf(userDo.getId()))
                .claim("account", userDo.getAccount())
                .claim("username", userDo.getUsername())
                .claim("role", userDo.getRole())
                .claim("sex", userDo.getSex())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(MultipartFile avatarFile, HttpServletRequest request) throws Exception {
        Long userId = UserContext.getUser().getId();
        // 1. 验证用户是否存在（通过领域服务）
        UserDo userDo = userDomainService.getUserById(userId);

        // 2. 验证文件类型和大小（应用层验证）
        validateAvatarFile(avatarFile);

        // 3. 使用基础设施服务保存文件
        String relativePath = fileStorageService.saveFile(avatarFile, "avatars");

        // 4. 构建完整的访问URL
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        String avatarUrl = scheme + "://" + serverName + ":" + serverPort + relativePath;

        // 5. 通过领域对象更新头像（执行业务规则）
        userDo.updateAvatar(avatarUrl);

        // 6. 通过领域服务持久化更新
        userDomainService.updateAvatarById(userDo);

        log.info("用户 {} 头像上传成功，路径: {}", userId, avatarUrl);

        return avatarUrl;
    }

    @Override
    public void updateMobile(Long userId, String mobile) {
        userDomainService.bindMobile(userId, mobile);
    }

    /**
     * 验证头像文件
     */
    private void validateAvatarFile(MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new IllegalArgumentException("头像文件不能为空");
        }

        // 检查文件类型
        String contentType = avatarFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只支持图片格式的文件");
        }

        // 检查文件大小（限制为5MB）
        if (avatarFile.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("头像文件大小不能超过5MB");
        }
    }

    @Override
    public IPage<User> pageUsers(int current, int size, User user, String startTime, String endTime) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(User::getDelFlag, 0)
                .like(StringUtils.hasText(user.getUsername()), User::getUsername, user.getUsername())
                .eq(StringUtils.hasText(user.getMobile()), User::getMobile, user.getMobile())
                .eq(StringUtils.hasText(user.getEmail()), User::getEmail, user.getEmail())
                .eq(StringUtils.hasText(user.getStatus()), User::getStatus, user.getStatus())
                .eq(StringUtils.hasText(user.getRole()), User::getRole, user.getRole())
                .eq(StringUtils.hasText(user.getSex()), User::getSex, user.getSex());

        if (StringUtils.hasText(startTime)) {
            wrapper.ge(User::getCreateTime, startTime);
        }
        if (StringUtils.hasText(endTime)) {
            wrapper.le(User::getCreateTime, endTime);
        }

        wrapper.orderByDesc(User::getCreateTime);

        return this.page(page, wrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        UserDo userDo = userDomainService.getUserById(user.getId());

        BeanUtils.copyProperties(user, userDo, "id", "account", "password");

        userDomainService.updateUser(userDo);
        log.info("更新用户成功，用户ID: {}", user.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null || user.getDelFlag() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setDelFlag(1);
        user.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(user);
        log.info("删除用户成功，用户ID: {}", id);
        return result;
    }

    private void saveUserToCache(String token, UserDo userDo) {
        try {
            // 直接传递对象，由RedisTemplate自动序列化
            cacheService.setUserCache(token, userDo, 7200);
            log.info("用户信息已保存到缓存，token: {}", token);
        } catch (Exception e) {
            log.error("保存用户信息到缓存失败", e);
        }
    }
}