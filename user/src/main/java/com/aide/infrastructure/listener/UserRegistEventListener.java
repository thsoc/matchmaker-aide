package com.aide.infrastructure.listener;

import com.aide.domain.event.UserRegistedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author mazg
 * @description 用户注册监听器
 * @date 2026/5/20
 * @date 11:26
 */
@Slf4j
@Component
public class UserRegistEventListener {


    /**
     * 监听用户注册事件
     *
     * 执行时机：事务成功提交后
     */
    @Async("loginEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserLoggedIn(UserRegistedEvent event) {
        //todo 发送mq去创建用户资金账户
        log.info("用户注册成功，发送mq去创建用户资金账户");
        log.info("用户注册成功，发送mq去创建用户资金账户,用户ID: {}", event.getUserId());
    }
}
