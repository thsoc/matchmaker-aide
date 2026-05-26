package com.aide.infrastructure.sms;

/**
 * @author mazg
 * @description 短信发送服务接口
 * @date 12:57 2026/5/26
 * @return
 **/
public interface SmsSender {
    /**
     * 发送短信
     * @param mobile 手机号
     * @param code 验证码
     */
    void send(String mobile, String code);
}
