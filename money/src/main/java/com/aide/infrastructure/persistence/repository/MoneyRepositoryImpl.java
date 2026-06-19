package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.MoneyDo;
import com.aide.domain.repository.MoneyRepository;
import com.aide.infrastructure.persistence.entity.Money;
import com.aide.infrastructure.persistence.mapper.MoneyMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 资金仓储实现 - 负责领域对象与持久化实体的转换
 * <p>
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
            log.debug("更新账户，用户ID: {}, 余额: {}", moneyDo.getUserId(), moneyDo.getAvailableMoney());
        }
        return convertToDomainObject(money);
    }

    public int freezeMoney(MoneyDo account) {
        LambdaUpdateWrapper<Money> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Money::getId, account.getId())
                .apply("available_money >= {0}", account.getFrozenMoney())
                .setSql("available_money = available_money - " + account.getFrozenMoney()
                        + ", frozen_money = frozen_money + " + account.getFrozenMoney());
        int update = moneyMapper.update(null, wrapper);
        return update;
    }

    @Override
    public void confirmFreeze(MoneyDo account) {
        new LambdaUpdateChainWrapper<>(moneyMapper)
                .eq(Money::getId, account.getId())
                .apply("frozen_money >= {0}", account.getFrozenMoney())
                .setSql("frozen_money = frozen_money - " + account.getFrozenMoney())
                .update();
    }

    /**
     * Cancel 阶段：冻结金额转入可用余额
     * @param account
     */
    @Override
    public void unfreezeMoney(MoneyDo account) {
        LambdaUpdateWrapper<Money> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Money::getId, account.getId())
                .apply("frozen_money >= {0}", account.getFrozenMoney())
                .setSql("available_money = available_money + " + account.getFrozenMoney()
                        + ", frozen_money = frozen_money - " + account.getFrozenMoney());
        moneyMapper.update(null, wrapper);
    }

    /**
     * 持久化实体 → 领域对象
     */
    private MoneyDo convertToDomainObject(Money entity) {
        return MoneyDo.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .availableMoney(entity.getAvailableMoney())
                .frozenMoney(entity.getFrozenMoney())
                .build();
    }

    /**
     * 领域对象 → 持久化实体
     */
    private Money convertToEntity(MoneyDo domainObject) {
        return Money.builder()
                .id(domainObject.getId())
                .userId(domainObject.getUserId())
                .availableMoney(domainObject.getAvailableMoney())
                .frozenMoney(domainObject.getFrozenMoney())
                .build();
    }
}
