package com.aide.service;

import com.aide.common.dto.type.OrderTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description 订单类型工厂
 * @date 2026/6/28
 * @date 13:02
 */
@Component
@RequiredArgsConstructor
public class OrderTypeFactory {
    private final List<OrderServiceStrategy> strategies;

    private Map<OrderTypeEnum, OrderServiceStrategy> strategyMap;

    @PostConstruct
    public void init() {
        // 自动注册所有策略到Map中
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        OrderServiceStrategy::getOrderType,
                        Function.identity()
                ));
    }

    /**
     * 根据会员类型获取配置
     */
    public OrderServiceStrategy getStrategy(OrderTypeEnum orderTypeEnum) {
        OrderServiceStrategy strategy = strategyMap.get(orderTypeEnum);
        if (strategy == null) {
            throw new IllegalArgumentException("无效的订单类型: " + orderTypeEnum);
        }
        return strategy;
    }
}
