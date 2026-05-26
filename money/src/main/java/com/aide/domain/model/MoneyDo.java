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
    private BigDecimal money;
}
