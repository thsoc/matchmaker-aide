package com.aide.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author mazg
 * @description 购买优惠券后事件对象
 * @date 2026/6/23
 * @date 15:35
 */
@Getter
public class ReceiveCouponEvent extends ApplicationEvent {
    private final Long id;
    private final Long userId;

    public ReceiveCouponEvent(Object source, Long id, Long userId) {
        super(source);
        this.id = id;
        this.userId = userId;
    }
}
