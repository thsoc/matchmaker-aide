package com.aide.controller;

import com.aide.common.Result;
import com.aide.entity.VO.LoginRequest;
import com.aide.entity.VO.LoginResponse;
import com.aide.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.aide.entity.VO.RegisterRequest;

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
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest.getAccount(), loginRequest.getPassword());
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
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            LoginResponse response = userService.register(registerRequest);
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
     * 获取用户信息 - GET 请求，带路径参数
     */
    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id) {
        return "获取用户ID: " + id;
    }

    /**
     * 更新用户信息 - PUT 请求
     */
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id) {
        return "更新用户ID: " + id;
    }

    /**
     * 删除用户 - DELETE 请求
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "删除用户ID: " + id;
    }
}
