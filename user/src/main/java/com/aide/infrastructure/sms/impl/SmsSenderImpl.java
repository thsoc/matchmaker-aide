package com.aide.infrastructure.sms.impl;

import com.aide.infrastructure.sms.SmsSender;
import com.aide.infrastructure.sms.strategy.SmsSenderContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 短信发送服务实现类
 * @date 2026/5/26
 * @date 12:57
 */
@Slf4j
@Component
public class SmsSenderImpl implements SmsSender {

    private final SmsSenderContext smsSenderContext;

    public SmsSenderImpl(SmsSenderContext smsSenderContext) {
        this.smsSenderContext = smsSenderContext;
    }

    @Override
    public void send(String mobile, String code) {
        smsSenderContext.send(mobile, code);
    }
}
