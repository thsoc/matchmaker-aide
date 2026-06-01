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
    private String password;
    private String description;
    private String introduce;
    private String role;
    private String status;
    private String sex;
    private String avatar;
    private String email;
    private String mobile;
    private String birthday;
    private BigDecimal income;
    private String occupation;
    private Integer integral;
    private Integer loginCount;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer delFlag;
    private String createBy;
    private String updateBy;
    private Integer version;
}
