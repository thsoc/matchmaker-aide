
package com.aide.service.impl;

import com.aide.common.IpUtils;
import com.aide.domain.UserDomainService;
import com.aide.entity.DO.UserDo;
import com.aide.entity.PO.User;
import com.aide.entity.VO.LoginResponse;
import com.aide.entity.VO.RegisterRequest;
import com.aide.mapper.UserMapper;
import com.aide.service.CacheService;
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

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserDomainService userDomainService;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;
    private final SecretKey jwtSecretKey;
    private final Long jwtExpiration;

    public UserServiceImpl(UserDomainService userDomainService,
                           CacheService cacheService,
                           ObjectMapper objectMapper,
                           SecretKey jwtSecretKey,
                           @Value("${jwt.expiration:7200}") Long jwtExpiration) {
        this.userDomainService = userDomainService;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
        this.jwtSecretKey = jwtSecretKey;
        this.jwtExpiration = jwtExpiration;
    }


    @Override
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
    public boolean createUser(User user, HttpServletRequest request) {
        UserDo userDo = new UserDo();
        BeanUtils.copyProperties(user, userDo);

        UserDo createdUser = userDomainService.createUser(userDo, IpUtils.getIpAddress(request));

        BeanUtils.copyProperties(createdUser, user);
        log.info("创建用户成功，用户ID: {}, 账号: {}", user.getId(), user.getAccount());
        return true;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        boolean result = this.removeByIds(ids);
        log.info("批量删除用户成功，删除数量: {}", ids.size());
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status, String updateBy) {
        UserDo userDo = userDomainService.getUserById(id);

        if ("NORMAL".equals(status)) {
            userDo.activate();
        } else if ("DISABLED".equals(status)) {
            userDo.deactivate();
        } else if ("BANNED".equals(status)) {
            userDo.ban();
        }

        userDo.updateAuditInfo(updateBy, LocalDateTime.now());
        userDomainService.updateUser(userDo);
        log.info("更新用户状态成功，用户ID: {}, 新状态: {}", id, status);
        return true;
    }

    @Override
    public Integer countUsers(String status, String role, String startTime, String endTime) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDelFlag, 0)
                .eq(StringUtils.hasText(status), User::getStatus, status)
                .eq(StringUtils.hasText(role), User::getRole, role);

        if (StringUtils.hasText(startTime)) {
            wrapper.ge(User::getCreateTime, startTime);
        }
        if (StringUtils.hasText(endTime)) {
            wrapper.le(User::getCreateTime, endTime);
        }

        return Math.toIntExact(this.count(wrapper));
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