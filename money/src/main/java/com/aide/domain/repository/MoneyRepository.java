package com.aide.domain.repository;


import com.aide.domain.model.MoneyDo;

/**
 * @author mazg
 * @description 资金仓储接口 - 定义领域对象的持久化契约
 *
 * 职责：
 * 1. 提供账户的查询和保存能力
 * 2. 隐藏基础设施层的实现细节
 * 3. 领域服务只依赖此接口，不直接操作 Mapper
 *
 * @date 2026/5/28
 */
public interface MoneyRepository {

    /**
     * 根据用户ID查询账户
     *
     * @param userId 用户ID
     * @return 账户领域对象，不存在时返回 null
     */
    MoneyDo findByUserId(Long userId);

    /**
     * 保存账户（新增或更新）
     *
     * @param moneyDo 账户领域对象
     */
    void save(MoneyDo moneyDo);
}
