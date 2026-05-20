package com.aide.controller;

import com.aide.common.IpUtils;
import com.aide.common.Result;
import com.aide.entity.VO.LoginRequest;
import com.aide.entity.VO.LoginResponse;
import com.aide.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.aide.entity.VO.RegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user") // 类级别映射，所有方法路径都会加上 /user 前缀
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
        try {
            LoginResponse response = userService.login(loginRequest.getAccount(), loginRequest.getPassword(), IpUtils.getIpAddress(request));
            return Result.success("登录成功", response);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册接口 - POST 请求
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        try {
            LoginResponse response = userService.register(registerRequest, IpUtils.getIpAddress(request));
            return Result.success("注册成功", response);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
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
