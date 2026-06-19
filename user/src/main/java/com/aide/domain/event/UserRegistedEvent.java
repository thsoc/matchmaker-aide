package com.aide.domain.event;

/**
 * @author mazg
 * @description 用户注册事件
 * @date 2026/5/20
 * @date 11:26
 */

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserRegistedEvent {
    private final Long userId;

    public UserRegistedEvent(Long userId) {
        this.userId = userId;
    }
}