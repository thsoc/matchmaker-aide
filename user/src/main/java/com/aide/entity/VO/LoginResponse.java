package com.aide.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 20721
 * @description 登录响应参数
 * @date 2026/5/15
 * @date 12:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String account;
    private String username;
    private String token; // JWT令牌或其他形式的认证令牌
    private String role;
    private String status;
}