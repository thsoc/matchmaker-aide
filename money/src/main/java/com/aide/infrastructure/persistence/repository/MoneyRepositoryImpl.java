package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.MoneyDo;
import com.aide.domain.repository.MoneyRepository;
import com.aide.infrastructure.persistence.entity.Money;
import com.aide.infrastructure.persistence.mapper.MoneyMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 资金仓储实现 - 负责领域对象与持久化实体的转换
 *
 * 职责：
 * 1. 实现领域层定义的 Repository 接口
 * 2. 处理 MoneyDo ↔ Money 的转换
 * 3. 调用 MyBatis-Plus Mapper 进行数据库操作
 * @date 2026/5/28
 * @date 11:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyRepositoryImpl implements MoneyRepository {

    private final MoneyMapper moneyMapper;

    @Override
    public MoneyDo findByUserId(Long userId) {
        LambdaQueryWrapper<Money> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Money::getUserId, userId);
        Money money = moneyMapper.selectOne(queryWrapper);

        if (money == null) {
            return null;
        }

        // 转换为领域对象
        return convertToDomainObject(money);
    }

    @Override
    public MoneyDo save(MoneyDo moneyDo) {
        Money money = convertToEntity(moneyDo);

        if (moneyDo.getId() == null) {
            moneyMapper.insert(money);
            log.debug("新增账户，用户ID: {}", moneyDo.getUserId());
        } else {
            moneyMapper.updateById(money);
            log.debug("更新账户，用户ID: {}, 余额: {}", moneyDo.getUserId(), moneyDo.getMoney());
        }
        return convertToDomainObject(money);
    }

    /**
     * 持久化实体 → 领域对象
     */
    private MoneyDo convertToDomainObject(Money entity) {
        return MoneyDo.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .money(entity.getMoney())
                .build();
    }

    /**
     * 领域对象 → 持久化实体
     */
    private Money convertToEntity(MoneyDo domainObject) {
        return Money.builder()
                .id(domainObject.getId())
                .userId(domainObject.getUserId())
                .money(domainObject.getMoney())
                .build();
    }
}
