package com.aide.entity.VO;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * @author mazg
 * @description 登录请求参数
 * @date 2026/5/15
 * @date 12:28
 */
@Data
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}
