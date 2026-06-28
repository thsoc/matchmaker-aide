package com.aide.service;

import com.aide.common.dto.type.PayTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description 支付类型工厂
 * @date 2026/6/28
 * @date 13:02
 */
@Component
@RequiredArgsConstructor
public class OrderPayTypeFactory {
    private final List<OrderPayStrategy> strategies;

    private Map<PayTypeEnum, OrderPayStrategy> strategyMap;

    @PostConstruct
    public void init() {
        // 自动注册所有策略到Map中
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        OrderPayStrategy::getPayType,
                        Function.identity()
                ));
    }

    /**
     * 根据会员类型获取配置
     */
    public OrderPayStrategy getStrategy(PayTypeEnum payTypeEnum) {
        OrderPayStrategy strategy = strategyMap.get(payTypeEnum);
        if (strategy == null) {
            throw new IllegalArgumentException("无效的支付类型: " + payTypeEnum);
        }
        return strategy;
    }
}
