package com.aide.service;

import com.aide.entity.PO.User;
import com.aide.entity.VO.LoginResponse;
import com.aide.entity.VO.RegisterRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService extends IService<User> {

    IPage<User> pageUsers(int current, int size, User user, String startTime, String endTime);


    boolean updateUser(User user);

    boolean deleteUser(Long id);

    /**
     * 用户登录
     *
     * @param account  用户账号
     * @param password 用户密码
     * @param loginIp
     * @return 登录后的用户响应
     */
    LoginResponse login(String account, String password, String loginIp);

    /**
     * 用户注册
     *
     * @param registerRequest 注册用户信息
     * @param loginIp
     * @return 注册后的用户响应
     */
    LoginResponse register(RegisterRequest registerRequest, String loginIp);

    /**
     * 上传用户头像
     * @param
     * @param avatarFile 头像文件
     * @param request HTTP请求对象
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile avatarFile, HttpServletRequest request) throws Exception;

}
