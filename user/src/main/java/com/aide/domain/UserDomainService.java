package com.aide.domain;

import com.aide.domain.event.UserLoggedInEvent;
import com.aide.entity.DO.UserDo;
import com.aide.mapper.UserMapper;
import com.aide.entity.PO.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserDomainService {

    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UserDomainService(UserMapper userMapper,
                             ApplicationEventPublisher eventPublisher) {
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }
    
    public UserDo createUser(UserDo userDo, String registerIp) {
        validateUser(userDo);
        checkUniqueness(userDo);

        userDo.initializeNewUser();

        userDo.record(registerIp);

        User user = convertToPo(userDo);
        userMapper.insert(user);

        userDo = convertToDo(user);
        return userDo;
    }



    
    public void updateUser(UserDo userDo) {
        validateUser(userDo);
        userMapper.updateAvatarById(userDo.getAvatar(),userDo.getId());
    }
    
    public UserDo getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDelFlag() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToDo(user);
    }
    
    public UserDo getUserByAccount(String account) {
        User user = userMapper.selectByAccount(account);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToDo(user);
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

//        // 5. 持久化领域对象
//        updateUser(userDo);

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
        User existingUser = userMapper.selectByAccount(userDo.getAccount());
        if (existingUser != null) {
            throw new IllegalArgumentException("账号已存在");
        }
        if (userDo.getMobile() != null) {
            User byMobile = userMapper.selectByMobile(userDo.getMobile());
            if (byMobile != null) {
                throw new IllegalArgumentException("手机号已被使用");
            }
        }
    }
    
    private User convertToPo(UserDo userDo) {
        return new User().copy(userDo);
    }
    
    private UserDo convertToDo(User user) {
        return new UserDo().copy(user);
    }

}
