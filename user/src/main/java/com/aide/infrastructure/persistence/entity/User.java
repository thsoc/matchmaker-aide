package com.aide.infrastructure.persistence.entity;

import com.aide.domain.model.UserDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户模块实体类
 * @date 2026/5/14
 * @date 18:01
 */
@Data// 更推荐的方式
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("aide_user")
public class User {
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 账号
     */
    @TableField("account")
    private String account;
    
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 自我介绍
     */
    @TableField("introduce")
    private String introduce;
    
    /**
     * 角色
     */
    @TableField("role")
    private String role;
    
    /**
     * 状态
     */
    @TableField("status")
    private String status;
    
    /**
     * 性别
     */
    @TableField("sex")
    private String sex;
    
    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;
    
    /**
     * 生日
     */
    @TableField("birthday")
    private String birthday;
    
    /**
     * 收入
     */
    @TableField("income")
    private BigDecimal income;
    
    /**
     * 职业
     */
    @TableField("occupation")
    private String occupation;
    
    /**
     * 积分
     */
    @TableField("integral")
    private Integer integral;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;
    
    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    /**
     * 删除状态（逻辑删除）
     */
    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;
    
    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;
    
    /**
     * 修改人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 版本号
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 预留字段
     */
    @TableField("reserved1")
    private String reserved1;

    @TableField("reserved2")
    private String reserved2;

    @TableField("reserved3")
    private String reserved3;

    @TableField("reserved4")
    private String reserved4;

    @TableField("reserved5")
    private Integer reserved5;

    @TableField("reserved6")
    private Integer reserved6;

    @TableField("reserved7")
    private Integer reserved7;

    @TableField("reserved8")
    private Integer reserved8;

//    /**
//     * 添加 Builder
//     */
//    public static class Builder {
//        private User user;
//        public Builder() {
//            user = new User();
//        }
//        public Builder id(Long id) {
//            user.id = id;
//            return this;
//        }
//        public Builder account(String account) {
//            user.account = account;
//            return this;
//        }
//
//        public Builder username(String username) {
//            user.username = username;
//            return this;
//        }
//
//        public Builder password(String password) {
//            user.password = password;
//            return this;
//        }
//        public Builder description(String description) {
//            user.description = description;
//            return this;
//        }
//        public Builder introduce(String introduce) {
//            user.introduce = introduce;
//            return this;
//        }
//        public Builder role(String role) {
//            user.role = role;
//            return this;
//        }
//        public Builder status(String status) {
//            user.status = status;
//            return this;
//        }
//        public Builder sex(String sex) {
//            user.sex = sex;
//            return this;
//        }
//        public Builder avatar(String avatar) {
//            user.avatar = avatar;
//            return this;
//        }
//        public Builder email(String email) {
//            user.email = email;
//            return this;
//        }
//        public Builder mobile(String mobile) {
//            user.mobile = mobile;
//            return this;
//        }
//        public Builder birthday(String birthday) {
//            user.birthday = birthday;
//            return this;
//        }
//        public Builder income(BigDecimal income) {
//            user.income = income;
//            return this;
//        }
//        public Builder occupation(String occupation) {
//            user.occupation = occupation;
//            return this;
//        }
//        public Builder integral(Integer integral) {
//            user.integral = integral;
//            return this;
//        }
//        public Builder money(BigDecimal money) {
//            user.money = money;
//            return this;
//        }
//        public Builder loginCount(Integer loginCount) {
//            user.loginCount = loginCount;
//            return this;
//        }
//        public Builder lastLoginTime(LocalDateTime lastLoginTime) {
//            user.lastLoginTime = lastLoginTime;
//            return this;
//        }
//        public Builder lastLoginIp(String lastLoginIp) {
//            user.lastLoginIp = lastLoginIp;
//            return this;
//        }
//        public Builder createTime(LocalDateTime createTime) {
//            user.createTime = createTime;
//            return this;
//        }
//        public Builder updateTime(LocalDateTime updateTime) {
//            user.updateTime = updateTime;
//            return this;
//        }
//        public Builder delFlag(Integer delFlag) {
//            user.delFlag = delFlag;
//            return this;
//        }
//        public Builder createBy(String createBy) {
//            user.createBy = createBy;
//            return this;
//        }
//        public Builder updateBy(String updateBy) {
//            user.updateBy = updateBy;
//            return this;
//        }
//        public Builder version(Integer version) {
//            user.version = version;
//            return this;
//        }
//        public User build() {
//            return user;
//        }
//    }
    public User copy(UserDo user) {
        if (user == null){
            return null;
        }
        return this.builder()
                .id(user.getId())
                .account(user.getAccount())
                .username(user.getUsername())
                .password(user.getPassword())
                .description(user.getDescription())
                .introduce(user.getIntroduce())
                .role(user.getRole())
                .status(user.getStatus())
                .sex(user.getSex())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .birthday(user.getBirthday())
                .income(user.getIncome())
                .occupation(user.getOccupation())
                .integral(user.getIntegral())
                .loginCount(user.getLoginCount())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .delFlag(user.getDelFlag())
                .createBy(user.getCreateBy())
                .updateBy(user.getUpdateBy())
                .version(user.getVersion())
                .build();
    }

}
