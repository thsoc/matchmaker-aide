package com.aide.adapter.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author mazg
 * @description 用户注册请求参数
 * @date 2026/5/16
 * @date 12:43
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号格式不正确，只能包含字母、数字和下划线，长度为4-20位")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密码格式不正确，只能包含字母、数字和下划线，长度为6-20位")
    private String password;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "邮箱格式不正确")
    private String email;

    private String sex;

    private String birthday;

    private String occupation;
}
