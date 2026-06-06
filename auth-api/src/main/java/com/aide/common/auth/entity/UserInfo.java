package com.aide.common.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author mazg
 * @description 用户信息
 * @date 2026/5/14
 * @date 17:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String account;
    private String username;
    private String role;
    private String sex;
}
