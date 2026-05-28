package com.aide.domain.factory;


import com.aide.domain.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mazg
 * @description 支付策略工厂 - 根据支付方式获取对应的策略实现
 *
 * 职责：
 * 1. 管理所有支付策略
 * 2. 根据支付方式类型返回对应策略
 * 3. 支持动态注册新策略
 * @date 2026/5/28
 * @date 13:11
 */


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final List<PaymentStrategy> paymentStrategies;
    private final Map<Integer, PaymentStrategy> strategyMap = new ConcurrentHashMap<>();

    /**
     * 初始化策略映射
     */
    @PostConstruct
    public void init() {
        for (PaymentStrategy strategy : paymentStrategies) {
            strategyMap.put(strategy.getPaymentType(), strategy);
            log.info("注册支付策略: type={}, class={}",
                    strategy.getPaymentType(), strategy.getClass().getSimpleName());
        }
    }

    /**
     * 根据支付方式类型获取策略
     *
     * @param payType 支付方式类型
     * @return 对应的支付策略
     */
    public PaymentStrategy getStrategy(Integer payType) {
        if (payType == null) {
            throw new IllegalArgumentException("支付方式不能为空");
        }

        PaymentStrategy strategy = strategyMap.get(payType);
        if (strategy == null) {
            throw new UnsupportedOperationException("不支持的支付方式: " + payType);
        }

        return strategy;
    }

    /**
     * 注册新的支付策略
     *
     * @param strategy 支付策略
     */
    public void registerStrategy(PaymentStrategy strategy) {
        strategyMap.put(strategy.getPaymentType(), strategy);
        log.info("动态注册支付策略: type={}", strategy.getPaymentType());
    }
}
