package com.aide.entity.DO;

import com.aide.entity.PO.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author 20721
 * @description 用户领域对象
 * @date 2026/5/14
 * @date 17:47
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDo {
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
    private BigDecimal money;
    private Integer loginCount;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer delFlag;
    private String createBy;
    private String updateBy;
    private Integer version;


    /**
     * 初始化新用户默认值 - 领域方法
     * 这是创建新用户时的领域规则
     */
    public void initializeNewUser() {
        // 新用户默认状态为正常
        if (this.status == null || this.status.isEmpty()) {
            this.status = "NORMAL";
        }

        // 新用户默认角色为普通用户
        if (this.role == null || this.role.isEmpty()) {
            this.role = "USER";
        }

        // 初始化积分和余额
        if (this.integral == null) {
            this.integral = 0;
        }
        if (this.money == null) {
            this.money = BigDecimal.ZERO;
        }
        if (this.loginCount == null) {
            this.loginCount = 0;
        }
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    /**
     * 验证登录 - 领域方法
     * 封装登录的核心业务规则
     * @param password 输入的密码
     */
    public void validateLogin(String password) {
        // 验证密码
        if (password == null || !password.equals(this.password)) {
            throw new IllegalArgumentException("密码错误");
        }

        // 验证用户状态
        if (!isActive()) {
            throw new IllegalStateException("用户账户已被禁用");
        }
    }


    /**
     * @author mazg
     * @description 充值
     * @date 19:08 2026/5/14
     **/
    public void recharge(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        this.money = this.money == null ? amount : this.money.add(amount);
        this.integral = this.integral == null ? amount.intValue() : this.integral + amount.intValue();
    }

    /**
     * @return
     * @author mazg
     * @description 提现
     * @date 19:08 2026/5/14
     **/
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("提现金额必须大于0");
        }
        if (this.money == null || this.money.compareTo(amount) < 0) {
            throw new IllegalStateException("余额不足");
        }
        this.money = this.money.subtract(amount);
    }

    public void updateProfile(String username, String email, String mobile, String avatar) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        }
        if (email != null && !email.trim().isEmpty()) {
            this.email = email;
        }
        if (mobile != null && !mobile.trim().isEmpty()) {
            this.mobile = mobile;
        }
        if (avatar != null && !avatar.trim().isEmpty()) {
            this.avatar = avatar;
        }
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于6位");
        }
        this.password = newPassword;
    }

    public void record(String ip) {
        this.lastLoginTime = LocalDateTime.now();
        this.lastLoginIp = ip;
        this.loginCount = this.loginCount == null ? 1 : this.loginCount + 1;
        this.createBy = getAccount();
        this.updateBy = getAccount();
        this.updateTime = LocalDateTime.now();
    }

    public void activate() {
        this.status = "NORMAL";
    }

    public void deactivate() {
        this.status = "DISABLED";
    }

    public void ban() {
        this.status = "BANNED";
    }

    public boolean isActive() {
        return "NORMAL".equals(this.status);
    }

    public boolean isBanned() {
        return "BANNED".equals(this.status);
    }

    public void addIntegral(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("积分必须为正数");
        }
        this.integral = this.integral == null ? points : this.integral + points;
    }

    public void deductIntegral(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("扣除积分必须为正数");
        }
        if (this.integral == null || this.integral < points) {
            throw new IllegalStateException("积分不足");
        }
        this.integral -= points;
    }

    public void updateAuditInfo(String updateBy, LocalDateTime updateTime) {
        this.updateBy = updateBy;
        this.updateTime = updateTime;
    }



    //    /**
//     * 添加 Builder
//     */
//    public static class Builder {
//        private UserDo userDo;
//        public Builder() {
//            userDo = new UserDo();
//        }
//        public Builder id(Long id) {
//            userDo.id = id;
//            return this;
//        }
//        public Builder account(String account) {
//            userDo.account = account;
//            return this;
//        }
//
//        public Builder username(String username) {
//            userDo.username = username;
//            return this;
//        }
//
//        public Builder password(String password) {
//            userDo.password = password;
//            return this;
//        }
//        public Builder description(String description) {
//            userDo.description = description;
//            return this;
//        }
//        public Builder introduce(String introduce) {
//            userDo.introduce = introduce;
//            return this;
//        }
//        public Builder role(String role) {
//            userDo.role = role;
//            return this;
//        }
//        public Builder status(String status) {
//            userDo.status = status;
//            return this;
//        }
//        public Builder sex(String sex) {
//            userDo.sex = sex;
//            return this;
//        }
//        public Builder avatar(String avatar) {
//            userDo.avatar = avatar;
//            return this;
//        }
//        public Builder email(String email) {
//            userDo.email = email;
//            return this;
//        }
//        public Builder mobile(String mobile) {
//            userDo.mobile = mobile;
//            return this;
//        }
//        public Builder birthday(String birthday) {
//            userDo.birthday = birthday;
//            return this;
//        }
//        public Builder income(BigDecimal income) {
//            userDo.income = income;
//            return this;
//        }
//        public Builder occupation(String occupation) {
//            userDo.occupation = occupation;
//            return this;
//        }
//        public Builder integral(Integer integral) {
//            userDo.integral = integral;
//            return this;
//        }
//        public Builder money(BigDecimal money) {
//            userDo.money = money;
//            return this;
//        }
//        public Builder loginCount(Integer loginCount) {
//            userDo.loginCount = loginCount;
//            return this;
//        }
//        public Builder lastLoginTime(LocalDateTime lastLoginTime) {
//            userDo.lastLoginTime = lastLoginTime;
//            return this;
//        }
//        public Builder lastLoginIp(String lastLoginIp) {
//            userDo.lastLoginIp = lastLoginIp;
//            return this;
//        }
//        public Builder createTime(LocalDateTime createTime) {
//            userDo.createTime = createTime;
//            return this;
//        }
//        public Builder updateTime(LocalDateTime updateTime) {
//            userDo.updateTime = updateTime;
//            return this;
//        }
//        public Builder delFlag(Integer delFlag) {
//            userDo.delFlag = delFlag;
//            return this;
//        }
//        public Builder createBy(String createBy) {
//            userDo.createBy = createBy;
//            return this;
//        }
//        public Builder updateBy(String updateBy) {
//            userDo.updateBy = updateBy;
//            return this;
//        }
//        public Builder version(Integer version) {
//            userDo.version = version;
//            return this;
//        }
//        public UserDo build() {
//            return userDo;
//        }
//    }
    public UserDo copy(User user) {
        if (user == null) {
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
                .money(user.getMoney())
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
