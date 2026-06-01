package com.aide.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author mazg
 * @description 会员类型配置 - 值对象
 * @date 2026/5/29
 * @date 13:07
 */
/**
 * @author mazg
 * @description 会员类型配置 - 值对象
 * @date 2026/5/29
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberTypeConfig {

    private Integer memberType;
    private String name;
    private BigDecimal price;
    private Integer validityDays;
}
