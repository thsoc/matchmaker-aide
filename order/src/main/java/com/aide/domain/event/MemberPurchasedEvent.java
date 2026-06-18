package com.aide.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class MemberPurchasedEvent extends ApplicationEvent {

    private final Long userId;
//    private final Integer giftPoints;
    private final String OrderNo;
    private final BigDecimal amount;

    public MemberPurchasedEvent(Object source, Long userId, String OrderNo, BigDecimal amount) {
        super(source);
        this.userId = userId;
        this.OrderNo = OrderNo;
        this.amount = amount;
    }
}