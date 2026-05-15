package com.aide.domain;

import com.aide.entity.DO.UserDo;
import com.aide.mapper.UserMapper;
import com.aide.entity.PO.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class UserDomainService {
    
    private final UserMapper userMapper;
    
    public UserDomainService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public UserDo createUser(UserDo userDo) {
        validateUser(userDo);
        checkUniqueness(userDo);

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
