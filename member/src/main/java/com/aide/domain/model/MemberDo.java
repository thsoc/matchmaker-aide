package com.aide.domain.model;

import com.aide.common.dto.feign.member.MemberTypeConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 会员领域对象
 * @date 2026/5/29
 * @date 12:38
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDo {
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会员类型：1-普通会员 2-高级会员 3-VIP会员
     */
    private Integer memberType;

    /**
     * 会员状态：0-未激活 1-已激活 2-已过期
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        return this.endTime != null && LocalDateTime.now().isAfter(this.endTime);
    }

    /**
     * 激活会员（新购买）
     */
    public void activate(MemberTypeConfig config) {
        LocalDateTime now = LocalDateTime.now();
        this.memberType = config.getMemberType();
        this.price = config.getPrice();
        this.status = 1;
        this.startTime = now;
        this.endTime = now.plusDays(config.getValidityDays());
    }

    /**
     * 续费会员（已有会员续期）
     */
    public void renew(MemberTypeConfig config) {
        LocalDateTime now = LocalDateTime.now();

        // 如果会员未过期，从当前结束时间续期；否则从现在开始
        LocalDateTime baseTime = (this.status == 1 && !isExpired()) ? this.endTime : now;

        this.memberType = config.getMemberType();
        this.price = config.getPrice();
        this.status = 1;
        this.startTime = now;
        this.endTime = baseTime.plusDays(config.getValidityDays());
        this.updateTime = now;
        this.updateBy = this.userId.toString();
        this.createBy = this.userId.toString();
    }

    /**
     * 计算本次购买应赠送的积分（一天一个积分）
     */
    public int calculateGiftPoints() {
        if (this.startTime == null || this.endTime == null) {
            return 0;
        }
        long days = java.time.Duration.between(this.startTime, this.endTime).toDays();
        return (int) days;
    }
}

