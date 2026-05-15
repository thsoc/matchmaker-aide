
package com.aide.service.impl;

import com.aide.domain.UserDomainService;
import com.aide.entity.DO.UserDo;
import com.aide.entity.PO.User;
import com.aide.entity.VO.LoginResponse;
import com.aide.mapper.UserMapper;
import com.aide.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserDomainService userDomainService;

    public UserServiceImpl(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }


    @Override
    public LoginResponse login(String account, String password) {
        // 通过领域服务获取用户信息
        UserDo userDo = userDomainService.getUserByAccount(account);

        // 验证密码（实际项目中应该使用加密比较）
        if (!password.equals(userDo.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        // 检查用户状态
        if (!userDo.isActive()) {
            throw new IllegalStateException("用户账户已被禁用");
        }
        // 记录登录信息
        String loginIp = "127.0.0.1"; // 实际项目中应从请求中获取真实IP
        userDo.recordLogin(loginIp);
        // 更新用户信息，时间更新，最后登录 时间
        userDomainService.updateUser(userDo);

        // 构建登录响应
        return LoginResponse.builder()
                .userId(userDo.getId())
                .account(userDo.getAccount())
                .username(userDo.getUsername())
                .token(generateToken(userDo)) // 生成JWT令牌
                .role(userDo.getRole())
                .status(userDo.getStatus())
                .build();
    }

    /**
     * 生成JWT令牌（简化版本，实际项目中需要使用JWT库）
     */
    private String generateToken(UserDo userDo) {
        // 这里只是一个示例，实际项目中应该使用JWT库生成真正的令牌
        return "token_" + userDo.getId() + "_" + System.currentTimeMillis();
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
    public boolean createUser(User user) {
        UserDo userDo = new UserDo();
        BeanUtils.copyProperties(user, userDo);

        UserDo createdUser = userDomainService.createUser(userDo);

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
    public boolean updateLastLoginInfo(Long id, String ip) {
        UserDo userDo = userDomainService.getUserById(id);
        userDo.recordLogin(ip);

        userDomainService.updateUser(userDo);
        log.debug("更新用户登录信息成功，用户ID: {}, IP: {}", id, ip);
        return true;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchImportUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("用户列表不能为空");
        }

        for (User user : users) {
            try {
                UserDo userDo = new UserDo();
                BeanUtils.copyProperties(user, userDo);
                userDomainService.createUser(userDo);
            } catch (Exception e) {
                log.warn("导入用户失败，账号: {}, 原因: {}", user.getAccount(), e.getMessage());
            }
        }

        log.info("批量导入用户完成，总数: {}", users.size());
        return true;
    }
}