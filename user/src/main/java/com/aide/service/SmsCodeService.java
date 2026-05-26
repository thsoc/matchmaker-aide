package com.aide.service;

public interface SmsCodeService {
    /**
     * 获取短信验证码
     * @param mobile 手机号
     * @param ipAddress IP地址
     */
    void getSmsCode(String mobile, String ipAddress);

    /**
     * 验证短信验证码
     * @param mobile 手机号
     * @param code 验证码
     */
    void verifySmsCode(String mobile, String code);

    /**
     * 绑定手机号
     * @param mobile 手机号
     * @param code 验证码
     * @param ipAddress IP地址
     */
    void bindMobile(String mobile, String code, String ipAddress);
}
