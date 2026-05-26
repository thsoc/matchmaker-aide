package com.aide.infrastructure.sms.strategy;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description 短信发送策略上下文
 * @date 2026/5/26
 * @date 14:05
 */
@Slf4j
@Component
public class SmsSenderContext {

    @Value("${sms.provider:mock}")
    private String provider;

    private final List<SmsStrategy> strategies;
    private Map<String, SmsStrategy> strategyMap;

    public SmsSenderContext(List<SmsStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostConstruct
    public void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        SmsStrategy::getProviderName,
                        strategy -> strategy
                ));

        log.info("已加载的短信策略: {}", strategyMap.keySet());
    }

    /**
     * 发送短信
     * @param mobile 手机号
     * @param code 验证码
     */
    public void send(String mobile, String code) {
        SmsStrategy strategy = strategyMap.get(provider);

        if (strategy == null) {
            log.warn("未找到短信提供商: {} 的策略，使用默认模拟发送", provider);
            strategy = strategyMap.get("mock");
        }

        log.debug("使用短信提供商: {}", provider);
        strategy.send(mobile, code);
    }
}

