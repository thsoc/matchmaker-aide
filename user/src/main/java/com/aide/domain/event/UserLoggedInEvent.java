package com.aide.domain.event;

/**
 * @author 20721
 * @description 用户登录事件
 * @date 2026/5/20
 * @date 11:26
 */
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserLoggedInEvent {
    private final Long userId;
    private final LocalDateTime loginTime;
    private final String loginIp;
    private final Integer loginCount;

    public UserLoggedInEvent(Long userId, LocalDateTime loginTime,
                             String loginIp, Integer loginCount) {
        this.userId = userId;
        this.loginTime = loginTime;
        this.loginIp = loginIp;
        this.loginCount = loginCount;
    }
}