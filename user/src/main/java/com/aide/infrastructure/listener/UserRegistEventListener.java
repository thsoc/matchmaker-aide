package com.aide.infrastructure.listener;

import com.aide.domain.event.UserLoggedInEvent;
import com.aide.infrastructure.persistence.entity.User;
import com.aide.infrastructure.persistence.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
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
    public void handleUserLoggedIn(UserLoggedInEvent event) {
        //todo 发送mq去创建用户账户
        log.info("用户注册成功，发送mq去创建用户账户");
        log.info("用户注册成功，用户ID: {}, 登录时间: {}, 登录IP: {}, 登录次数: {}",
                event.getUserId(), event.getLoginTime(), event.getLoginIp(), event.getLoginCount());
    }
}
