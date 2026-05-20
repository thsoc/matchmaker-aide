package com.aide.infrastructure.listener;

import com.aide.domain.event.UserLoggedInEvent;
import com.aide.entity.PO.User;
import com.aide.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author 20721
 * @description 用户登录事件监听器
 * @date 2026/5/20
 * @date 11:26
 */
@Slf4j
@Component
public class UserLoggedInEventListener {

    private final UserMapper userMapper;
    private final PlatformTransactionManager transactionManager;

    public UserLoggedInEventListener(UserMapper userMapper,
                                     PlatformTransactionManager transactionManager) {
        this.userMapper = userMapper;
        this.transactionManager = transactionManager;
    }

    /**
     * 监听用户登录事件
     *
     * 执行时机：事务成功提交后
     * 职责：更新数据库中的登录相关信息
     */
    @Async("loginEventExecutor")
    @TransactionalEventListener/*(phase = TransactionPhase.AFTER_COMMIT)*/
    public void handleUserLoggedIn(UserLoggedInEvent event) {
//        TransactionTemplate template = new TransactionTemplate(transactionManager);
//        template.execute(status -> {
//            return null;
//        });
        try {
            log.info("收到用户登录事件 - userId: {}, ip: {}",
                    event.getUserId(), event.getLoginIp());
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", event.getUserId())
                    .eq("del_flag", 0)
                    .set("last_login_time", event.getLoginTime())
                    .set("last_login_ip", event.getLoginIp())
                    .set("login_count", event.getLoginCount());

            //null表示更新所有字段
            int rows = userMapper.update(null, wrapper);

            log.info("用户登录信息更新完成 - userId: {}, 影响行数: {}",
                    event.getUserId(), rows);
        } catch (Exception e) {
            // 异步异常不会影响主流程
            // 需要记录日志或发送告警
            log.error("更新登录信息失败: userId={}", event.getUserId(), e);

//            // 发送到错误队列重试
//            errorQueue.send(event);
        }
    }
}
