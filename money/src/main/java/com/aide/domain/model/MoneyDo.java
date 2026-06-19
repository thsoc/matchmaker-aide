package com.aide.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 资金领域对象
 * @date 2026/5/25
 * @date 16:06
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyDo {
    private Long id;
    private Long userId;
    private BigDecimal availableMoney;
    private BigDecimal frozenMoney;


    // ==================== 业务规则方法 ====================

    /**
     * 增加余额（充值业务规则）
     *
     * 规则：
     * 1. 充值金额必须大于0
     */
    public void addBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        this.availableMoney = this.availableMoney.add(amount);
    }

    /**
     * 扣减余额（消费业务规则）
     *
     * 规则：
     * 1. 扣款金额必须大于0
     * 2. 余额必须充足
     */
    public void deductBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣款金额必须大于0");
        }
        if (this.availableMoney.compareTo(amount) < 0) {
            throw new IllegalStateException("余额不足");
        }
        this.availableMoney = this.availableMoney.subtract(amount);
    }


    /**
     * 验证余额是否充足
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0
                && this.availableMoney.compareTo(amount) >= 0;
    }

    /**
     * 获取可用余额
     */
    public BigDecimal getAvailableBalance() {
        return this.availableMoney;
    }

    /**
     * 创建新账户（工厂方法）
     *
     * 业务规则：新用户初始余额为0
     */
    public static MoneyDo createNewAccount(Long userId) {
        return MoneyDo.builder()
                .userId(userId)
                .availableMoney(BigDecimal.ZERO)
                .frozenMoney(BigDecimal.ZERO)
                .build();
    }

    public void freezeMoney(BigDecimal amount) {
        //提前判断
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣款金额必须大于0");
        }
        if (this.availableMoney.compareTo(amount) < 0) {
            throw new IllegalStateException("余额不足");
        }
//        this.availableMoney = this.availableMoney.subtract(amount);
//        this.frozenMoney = this.frozenMoney.add(amount);
        this.frozenMoney = amount;
    }


    public void confirmFreeze(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣款金额必须大于0");
        }
        if (this.frozenMoney.compareTo(amount) < 0) {
            throw new IllegalStateException("锁定金额不足");
        }
//        this.frozenMoney = this.frozenMoney.subtract(amount);
        this.frozenMoney = amount;
    }

    /**
     * @author mazg
     * @description Cancel 阶段：解冻资金，恢复可用余额
     * @date 20:30 2026/6/9
     * @return
     **/
    public void unfreezeMoney(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣款金额必须大于0");
        }
        if (this.frozenMoney.compareTo(amount) < 0) {
            throw new IllegalStateException("锁定金额不足");
        }
//        this.availableMoney = this.availableMoney.add(amount);
//        this.frozenMoney = this.frozenMoney.subtract(amount);
        this.frozenMoney = amount;

    }
}
