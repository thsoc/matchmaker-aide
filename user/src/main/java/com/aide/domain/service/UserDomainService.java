package com.aide.domain.service;

import com.aide.domain.event.UserLoggedInEvent;
import com.aide.domain.model.UserDo;
import com.aide.domain.repository.UserRepository;
import com.aide.infrastructure.persistence.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 创建新用户
     */
    public UserDo createUser(UserDo userDo, String registerIp) {
        validateUser(userDo);
        checkUniqueness(userDo);

        userDo.initializeNewUser();
        userDo.record(registerIp);

        userRepository.save(userDo);
        return userDo;
    }

    /**
     * 更新用户头像
     */
    public void updateAvatarById(UserDo userDo) {
        validateUser(userDo);
        userRepository.updateAvatar(userDo.getId(), userDo.getAvatar());
    }



    /**
     * 更新用户信息
     */
    public void updateUser(UserDo userDo) {
        validateUser(userDo);
        userRepository.save(userDo);
    }

    /**
     * 根据 ID 获取用户
     */
    public UserDo getUserById(Long id) {
        UserDo userDo = userRepository.findById(id);
        if (userDo == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return userDo;
    }


    /**
     * 根据账号获取用户
     */
    public UserDo getUserByAccount(String account) {
        UserDo userDo = userRepository.findByAccount(account);
        if (userDo == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return userDo;
    }

    /**
     * 用户登录 - 领域服务方法（纯DDD实现）
     *
     * 设计说明：
     * 1. 领域对象负责业务规则验证和状态变更
     * 2. 通过领域事件通知基础设施层进行持久化
     * 3. 监听器会使用 UpdateWrapper 只更新登录相关字段（性能优化）
     * 4. 此处不需要手动调用 updateUser，避免重复更新
     *
     * 职责：
     * 1. 获取用户
     * 2. 验证登录（密码、状态）
     * 3. 记录登录信息
     * 4. 发布领域事件（由监听器负责持久化）
     */
    public UserDo login(String account, String password, String loginIp) {
        // 1. 获取用户
        UserDo userDo = getUserByAccount(account);

        // 2. 验证登录（领域行为）
        userDo.validateLogin(password);

        // 3. 记录登录信息（领域行为，产生副作用）
        userDo.record(loginIp);

        // 4. 发布领域事件（告诉基础设施层发生了什么）
        eventPublisher.publishEvent(new UserLoggedInEvent(
                userDo.getId(),
                userDo.getLastLoginTime(),
                userDo.getLastLoginIp(),
                userDo.getLoginCount()
        ));

        return userDo;
    }


    private void validateUser(UserDo userDo) {
        if (userDo.getAccount() == null || userDo.getAccount().trim().isEmpty()) {
            throw new IllegalArgumentException("账号不能为空");
        }
        if (userDo.getPassword() == null || userDo.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
    }


    private void checkUniqueness(UserDo userDo) {
        UserDo existingUser = userRepository.findByAccount(userDo.getAccount());
        if (existingUser != null) {
            throw new IllegalArgumentException("账号已存在");
        }
        if (userDo.getMobile() != null) {
            UserDo byMobile = userRepository.findByMobile(userDo.getMobile());
            if (byMobile != null) {
                throw new IllegalArgumentException("手机号已被使用");
            }
        }
    }


    /**
     * 绑定手机号 - 领域服务方法
     * 职责：
     * 1. 获取用户
     * 2. 检查手机号唯一性
     * 3. 调用领域对象更新手机号
     * 4. 持久化变更
     */
    public void bindMobile(Long userId, String mobile) {
        // 1. 获取用户
        UserDo userDo = getUserById(userId);

        // 2. 检查手机号是否已被其他用户使用
        checkMobileUniqueness(mobile, userId);

        // 3. 通过领域对象绑定手机号（执行业务规则）
        userDo.bindMobile(mobile);

        // 4. 持久化更新手机号
        userRepository.updateMobile(userId, mobile);
    }

    /**
     * 检查手机号唯一性
     */
    private void checkMobileUniqueness(String mobile, Long excludeUserId) {
        UserDo existingUser = userRepository.findByMobile(mobile);
        if (existingUser != null && !existingUser.getId().equals(excludeUserId)) {
            throw new IllegalArgumentException("该手机号已被其他用户绑定");
        }
    }

    public IPage<User> getPageUsers(int current, int size, User user, String startTime, String endTime) {
        return userRepository.getPageUsers(current, size, user, startTime, endTime);
    }

    /**
     * 删除用户
     *
     * @return
     */
    public boolean deleteUser(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null || user.getDelFlag() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setDelFlag(1);
        user.setUpdateTime(LocalDateTime.now());

        boolean result = userRepository.deleteUserById(id);
        return result;
    }
}
