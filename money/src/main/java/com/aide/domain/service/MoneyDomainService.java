package com.aide.domain.service;

import com.aide.domain.event.PaymentFailureEvent;
import com.aide.domain.event.PaymentSuccessEvent;
import com.aide.domain.model.MoneyDo;
import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.repository.MoneyRepository;
import com.aide.domain.repository.RechargeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 资金领域服务 - 负责账户和充值相关的业务协调
 *
 * 职责：
 * 1. 管理用户账户（创建、查询、更新余额）
 * 2. 管理充值记录（创建、状态变更）
 * 3. 协调充值流程（更新充值记录 + 更新账户余额）
 *
 * @date 2026/5/28
 * @date 10:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyDomainService {

    private final MoneyRepository moneyRepository;
    private final RechargeRecordRepository rechargeRecordRepository;
    private final PaymentContext paymentContext;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    // ==================== 账户相关的方法 ====================

    /**
     * 查询或创建用户账户
     */
    public MoneyDo getOrCreateAccount(Long userId) {
        MoneyDo account = moneyRepository.findByUserId(userId);

        if (account == null) {
            // 创建新账户
            MoneyDo newAccount = MoneyDo.createNewAccount(userId);
            moneyRepository.save(newAccount);
            log.info("为用户创建新账户，用户ID: {}", userId);
            return newAccount;
        } else {
            return account;
        }
    }

    /**
     * 为用户充值（账户层面的业务逻辑）
     */
    @Transactional(rollbackFor = Exception.class)
    public void rechargeAccount(Long userId, BigDecimal amount) {
        MoneyDo account = getOrCreateAccount(userId);
        account.addBalance(amount);
        moneyRepository.save(account);

        log.info("账户充值成功，用户ID: {}, 充值金额: {}, 新余额: {}",
                userId, amount, account.getMoney());
    }

    // ==================== 充值记录相关的方法 ====================

    /**
     * 创建充值记录
     */
    public RechargeRecordDo createRechargeRecord(Long userId, BigDecimal amount, Integer payType, String remark) {
        RechargeRecordDo rechargeRecord = RechargeRecordDo.create(userId, amount, payType, remark);

        log.info("创建充值记录，用户ID: {}, 金额: {}, 订单号: {}",
                rechargeRecord.getUserId(), rechargeRecord.getAmount(), rechargeRecord.getOrderNo());

        return rechargeRecord;
    }

    /**
     * 保存充值记录
     */
    public void saveRechargeRecord(RechargeRecordDo domainObject) {
        rechargeRecordRepository.save(domainObject);
    }

    /**
     * 发起支付（调用第三方支付接口）
     */
    public String initiatePayment(RechargeRecordDo rechargeRecord) {
        // 1. 标记充值记录为"处理中"
        rechargeRecord.markAsProcessing();
        rechargeRecordRepository.save(rechargeRecord);

        // 2. 调用支付上下文执行支付
        String paymentResult = paymentContext.executePayment(rechargeRecord);

        log.info("支付已发起，订单号: {}, 支付结果: {}",
                rechargeRecord.getOrderNo(), paymentResult);

        return paymentResult;
    }

    /**
     * 充值成功处理（协调充值记录和账户余额）
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleRechargeSuccess(RechargeRecordDo rechargeRecord) {
        // 1. 更新充值记录状态
        rechargeRecord.markAsSuccess();
        rechargeRecordRepository.save(rechargeRecord);

        // 2. 更新账户余额
        rechargeAccount(rechargeRecord.getUserId(), rechargeRecord.getAmount());

        log.info("充值完成，用户ID: {}, 金额: {}, 订单号: {}",
                rechargeRecord.getUserId(), rechargeRecord.getAmount(), rechargeRecord.getOrderNo());
    }

    /**
     * 充值失败处理
     */
    public void handleRechargeFailure(RechargeRecordDo rechargeRecord, String failureReason) {
        rechargeRecord.markAsFailure(failureReason);
        rechargeRecordRepository.save(rechargeRecord);

        log.warn("充值失败，用户ID: {}, 原因: {}", rechargeRecord.getUserId(), failureReason);
    }

    /**
     * 查询支付状态并同步
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncPaymentStatus(String orderNo, Integer payType) {
        // 1. 查询第三方支付状态
        boolean isPaid = paymentContext.queryPaymentStatus(orderNo, payType);

        if (isPaid) {
            // 2. 如果已支付，处理充值成功
            RechargeRecordDo rechargeRecord = rechargeRecordRepository.findByOrderNo(orderNo);
            if (rechargeRecord != null && !rechargeRecord.isSuccess()) {
                handleRechargeSuccess(rechargeRecord);
            }
        }
    }

    /**
     * 处理支付回调 - 支付成功
 *      * 职责：
     *      * 1. 验证订单状态
     *      * 2. 更新充值记录
     *      * 3. 发布支付成功事件（由监听器更新余额）
     *  * 安全防护（三层防护）：
     *      * 1. Redis分布式锁 - 防止高并发（第一道防线）
     *      * 2. 数据库乐观锁 - 防止并发更新（第二道防线，最终保障）
     *      * 3. 状态检查 - 实现幂等性（快速失败）
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(String orderNo, String transactionId) {
        log.info("开始处理支付回调，订单号: {}, 交易号: {}", orderNo, transactionId);

        // ========== 使用 Redisson 分布式锁（带看门狗）==========
        String lockKey = "payment:callback:lock:" + orderNo;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁自动续期
            boolean locked = lock.tryLock(3, TimeUnit.SECONDS);

            if (!locked) {
                log.warn("支付回调正在处理中，订单号: {}", orderNo);
                throw new IllegalStateException("订单处理中，请勿重复提交");
            }

            // ========== 第二层防护：状态检查（快速失败）==========
            RechargeRecordDo rechargeRecord = rechargeRecordRepository.findByOrderNo(orderNo);
            if (rechargeRecord == null) {
                log.error("充值记录不存在，订单号: {}", orderNo);
                throw new IllegalArgumentException("充值记录不存在");
            }

            if (rechargeRecord.isSuccess()) {
                log.warn("充值记录已处理成功，订单号: {}, 交易号: {}", orderNo, transactionId);
                return;
            }

            // ========== 第三层防护：数据库乐观锁（最终保障）==========
            int updatedRows = rechargeRecordRepository.updateStatusToSuccess(orderNo, transactionId);

            if (updatedRows == 0) {
                log.warn("充值记录已被其他请求处理，订单号: {}", orderNo);
                return;
            }

            // 重新查询获取最新数据
            rechargeRecord = rechargeRecordRepository.findByOrderNo(orderNo);

            // 发布支付成功事件（只发布一次）
            eventPublisher.publishEvent(new PaymentSuccessEvent(rechargeRecord, transactionId));
//            // 修复：注册事务同步回调，在事务提交后再发布事件
//            TransactionSynchronizationManager.registerSynchronization(
//                    new TransactionSynchronizationAdapter() {
//                        @Override
//                        public void afterCommit() {
//                            // 事务提交成功后才发布事件
//                            eventPublisher.publishEvent(
//                                    new PaymentSuccessEvent(rechargeRecord, transactionId)
//                            );
//                            log.info("事务提交成功，发布支付成功事件，订单号: {}", orderNo);
//                        }
//                    }
//            );

            log.info("支付回调处理完成，订单号: {}, 用户ID: {}, 金额: {}",
                    orderNo, rechargeRecord.getUserId(), rechargeRecord.getAmount());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断，订单号: {}", orderNo, e);
            throw new RuntimeException("获取锁失败", e);
        } finally {
            // 释放锁（只有当前线程持有的锁才能释放）
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁，订单号: {}", orderNo);
            }
        }
    }

    /**
     * 处理支付回调 - 支付失败
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentFailure(String orderNo, String failureReason) {
        log.info("开始处理支付失败回调，订单号: {}, 原因: {}", orderNo, failureReason);

        String lockKey = "payment:callback:fail:lock:" + orderNo;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean locked = lock.tryLock(3, TimeUnit.SECONDS);

            if (!locked) {
                log.warn("支付失败回调正在处理中，订单号: {}", orderNo);
                return;
            }

            RechargeRecordDo rechargeRecord = rechargeRecordRepository.findByOrderNo(orderNo);
            if (rechargeRecord == null) {
                log.error("充值记录不存在，订单号: {}", orderNo);
                return;
            }

            if (rechargeRecord.isFailed()) {
                log.warn("充值记录已标记为失败，订单号: {}", orderNo);
                return;
            }

            int updatedRows = rechargeRecordRepository.updateStatusToFailure(orderNo, failureReason);

            if (updatedRows == 0) {
                log.warn("充值记录已被其他请求处理，订单号: {}", orderNo);
                return;
            }

            rechargeRecord = rechargeRecordRepository.findByOrderNo(orderNo);
            eventPublisher.publishEvent(new PaymentFailureEvent(rechargeRecord, failureReason));

            log.info("支付失败回调处理完成，订单号: {}", orderNo);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断，订单号: {}", orderNo, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

