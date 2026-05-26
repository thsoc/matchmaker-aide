package com.aide.infrastructure.sms.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description mock短信发送策略
 * @date 2026/5/26
 * @date 14:03
 */
@Slf4j
@Component
public class MockSmsStrategy implements SmsStrategy {

    @Override
    public void send(String mobile, String code) {
        log.info("【模拟发送短信】手机号: {}, 验证码: {}", mobile, code);
    }

    @Override
    public String getProviderName() {
        return "mock";
    }
}

