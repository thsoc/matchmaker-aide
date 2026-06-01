package com.aide.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;


/**
 * @author mazg
 * @description 会员购买成功事件
 * @date 2026/5/29
 * @date 13:12
 */
@Getter
public class MemberPurchasedEvent extends ApplicationEvent {

    private final Long userId;
    private final Long memberId;
    private final Integer memberType;
    private final BigDecimal amount;
    private final Integer giftPoints;

    public MemberPurchasedEvent(Object source, Long userId, Long memberId,
                                Integer memberType, BigDecimal amount, Integer giftPoints) {
        super(source);
        this.userId = userId;
        this.memberId = memberId;
        this.memberType = memberType;
        this.amount = amount;
        this.giftPoints = giftPoints;
    }
}
