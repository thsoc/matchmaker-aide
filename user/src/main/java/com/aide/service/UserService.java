package com.aide.service;

import com.aide.entity.PO.User;
import com.aide.entity.VO.LoginResponse;
import com.aide.entity.VO.RegisterRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService extends IService<User> {

    IPage<User> pageUsers(int current, int size, User user, String startTime, String endTime);

    boolean createUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(Long id);

    boolean deleteBatchUsers(List<Long> ids);

    boolean updateLastLoginInfo(Long id, String ip);

    boolean updateStatus(Long id, String status, String updateBy);

    Integer countUsers(String status, String role, String startTime, String endTime);

    boolean batchImportUsers(List<User> users);

    /**
     * 用户登录
     * @param account 用户账号
     * @param password 用户密码
     * @return 登录后的用户响应
     */
    LoginResponse login(String account, String password);

    /**
     * 用户注册
     * @param registerRequest 注册用户信息
     * @return 注册后的用户响应
     */
    LoginResponse register(RegisterRequest registerRequest);
}
