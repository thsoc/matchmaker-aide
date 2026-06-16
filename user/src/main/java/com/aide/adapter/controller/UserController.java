package com.aide.adapter.controller;

import com.aide.adapter.VO.LoginRequest;
import com.aide.adapter.VO.LoginResponse;
import com.aide.adapter.VO.RegisterRequest;
import com.aide.common.Result.IpUtils;
import com.aide.common.Result.Result;
import com.aide.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录接口 - POST 请求
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse response = userService.login(loginRequest.getAccount(), loginRequest.getPassword(), IpUtils.getIpAddress(request));
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册接口 - POST 请求
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        LoginResponse response = userService.register(registerRequest, IpUtils.getIpAddress(request));
        return Result.success("注册成功", response);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String avatarUrl = userService.uploadAvatar(file, request);
            return Result.success("头像上传成功", avatarUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
}
