package com.aide.service;

import com.aide.entity.PO.User;
import com.aide.entity.VO.LoginResponse;
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

    LoginResponse login(String account, String password);
}
