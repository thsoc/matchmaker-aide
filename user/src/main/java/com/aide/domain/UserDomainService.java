package com.aide.domain;

import com.aide.entity.DO.UserDo;
import com.aide.mapper.UserMapper;
import com.aide.entity.PO.User;
import org.springframework.stereotype.Component;

@Component
public class UserDomainService {
    
    private final UserMapper userMapper;
    
    public UserDomainService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public UserDo createUser(UserDo userDo, String registerIp) {
        validateUser(userDo);
        checkUniqueness(userDo);

        userDo.initializeNewUser();

        if (registerIp != null) {
            userDo.record(registerIp);
        }

        User user = convertToPo(userDo);
        userMapper.insert(user);

        userDo = convertToDo(user);
        return userDo;
    }



    
    public void updateUser(UserDo userDo) {
        validateUser(userDo);
        
        User user = convertToPo(userDo);
        userMapper.updateById(user);
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
     * 用户登录 - 领域服务方法
     * 职责：
     * 1. 获取用户
     * 2. 验证登录（密码、状态）
     * 3. 记录登录信息
     * 4. 更新用户
     */
    public UserDo login(String account, String password, String loginIp) {
        // 1. 获取用户
        UserDo userDo = getUserByAccount(account);

        // 2. 验证登录（通过领域方法）
        userDo.validateLogin(password);

        // 3. 记录登录信息（通过领域方法）
        userDo.record(loginIp);

        // 4. 更新用户
        updateUser(userDo);

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
