package com.aide.infrastructure.sms.strategy;

/**
 * @author mazg
 * @description 短信发送策略接口
 * @date 2026/5/26
 * @date 14:57
 */
public interface SmsStrategy {
    /**
     * 发送短信
     * @param mobile 手机号
     * @param code 验证码
     */
    void send(String mobile, String code);

    /**
     * 获取策略名称
     */
    String getProviderName();
}
