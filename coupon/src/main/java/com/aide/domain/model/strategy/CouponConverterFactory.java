package com.aide.domain.model.strategy;



import com.aide.domain.model.CouponDiscountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description 优惠券转换器工厂
 * @date 2026/6/21
 * @date 18:38
 */
@Component
public class CouponConverterFactory {

    private  Map<CouponDiscountType, CouponConverterStrategy> strategyMap = new HashMap<>();

    /**
     * 通过构造函数注入所有策略实现
     * Spring 会自动将所有 CouponConverterStrategy 的 Bean 注入到这个 List 中
     */
    @Autowired
    public CouponConverterFactory(List<CouponConverterStrategy> strategies) {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(CouponConverterStrategy::getCouponType, Function.identity()));
    }

    /**
     * 根据优惠券类型获取对应的转换器
     * @param couponDiscountType 优惠券折扣方式
     * @return 对应的转换器策略
     */
    public CouponConverterStrategy getConverter(int couponDiscountType) {
        CouponConverterStrategy strategy = strategyMap.get(couponDiscountType);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的优惠券类型: " + couponDiscountType);
        }
        return strategy;
    }
}