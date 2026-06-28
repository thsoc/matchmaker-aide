package com.aide.domain.factory;



import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.domain.strategy.MemberTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description  会员类型工厂
 *  *
 *  * 职责：根据会员类型返回对应的配置
 * @date 2026/5/29
 * @date 13:21
 */
@Component
@RequiredArgsConstructor
public class MemberTypeFactory {

    private final List<MemberTypeStrategy> strategies;

    private Map<Integer, MemberTypeStrategy> strategyMap;

    @PostConstruct
    public void init() {
        // 自动注册所有策略到Map中
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        MemberTypeStrategy::getTypeCode,
                        Function.identity()
                ));
    }

    /**
     * 根据会员类型获取配置
     */
    public MemberTypeConfig getConfig(Integer memberType) {
        MemberTypeStrategy strategy = strategyMap.get(memberType);
        if (strategy == null) {
            throw new IllegalArgumentException("无效的会员类型: " + memberType);
        }
        return strategy.getConfig();
    }
}
