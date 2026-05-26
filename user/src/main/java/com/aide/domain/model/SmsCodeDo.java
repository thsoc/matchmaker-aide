package com.aide.domain.model;

import lombok.Getter;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 短信验证码领域对象
 * @date 2026/5/26
 * @date 12:29
 */
@Getter
public class SmsCodeDo {

    private String mobile;
    private String code;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private String ipAddress;
    private boolean verified;

    private static final int CODE_LENGTH = 6;
    private static final SecureRandom secureRandom = new SecureRandom();

    public SmsCodeDo() {
    }

    public SmsCodeDo(String mobile, String ipAddress) {
        validateMobile(mobile);
        this.mobile = mobile;
        this.ipAddress = ipAddress;
        this.code = generateVerificationCode();
        this.createTime = LocalDateTime.now();
        this.verified = false;
    }

    private void validateMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!mobile.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }

    private String generateVerificationCode() {
        return String.format("%0" + CODE_LENGTH + "d", secureRandom.nextInt(1000000));
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isExpired() {
        if (expireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expireTime);
    }

    public void verify(String inputCode) {
        if (isExpired()) {
            throw new IllegalStateException("验证码已过期");
        }
        if (verified) {
            throw new IllegalStateException("验证码已被使用");
        }
        if (!this.code.equals(inputCode)) {
            throw new IllegalArgumentException("验证码错误");
        }
        this.verified = true;
    }
}
