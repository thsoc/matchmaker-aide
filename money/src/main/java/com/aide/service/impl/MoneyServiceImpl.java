package com.aide.service.impl;


import com.aide.adapter.converter.RechargeRecordConverter;
import com.aide.adapter.dto.RechargeRequestDTO;
import com.aide.adapter.dto.RechargeResponseDTO;
import com.aide.common.exception.MoneyException;
import com.aide.domain.event.PaymentFailureEvent;
import com.aide.domain.event.PaymentSuccessEvent;
import com.aide.domain.model.MoneyDo;
import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.repository.MoneyRepository;
import com.aide.domain.repository.RechargeRecordRepository;
import com.aide.domain.service.MoneyDomainService;
import com.aide.service.MoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author mazg
 * @description 资金应用服务实现 - 负责协调充值业务流程
 *
 * 职责：
 * 1. 接收 DTO 参数
 * 2. 调用领域服务执行业务逻辑
 * 3. 控制事务边界
 * 4. 转换领域对象为响应 DTO
 * @date 2026/5/25
 * @date 15:53
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {

    private final MoneyDomainService moneyDomainService;
    private final MoneyRepository moneyRepository;
    private final RechargeRecordRepository rechargeRecordRepository;
    private final RechargeRecordConverter rechargeRecordConverter;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    @Override
    public Long getMoney(String account) {
        Long userId = Long.parseLong(account);
        MoneyDo accountInfo = moneyRepository.findByUserId(userId);

        if (accountInfo == null) {
            log.warn("账户不存在，用户ID: {}", userId);
            return 0L;
        }

        return accountInfo.getMoney().longValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RechargeResponseDTO recharge(RechargeRequestDTO request) throws MoneyException {
        // 检查事务状态
        log.info("事务是否活跃: {}", TransactionSynchronizationManager.isActualTransactionActive());
        log.info("开始处理充值请求，用户ID: {}, 金额: {}", request.getUserId(), request.getAmount());

        try {
            // 1. 创建充值记录（调用领域服务）
            RechargeRecordDo rechargeRecord = moneyDomainService.createRechargeRecord(
                    request.getUserId(),
                    request.getAmount(),
                    request.getPayType(),
                    request.getRemark()
            );

            // 2. 保存充值记录
            RechargeRecordDo rechargeRecordDo = moneyDomainService.saveRechargeRecord(rechargeRecord);

            // 3. 发起第三方支付（调用微信/支付宝）
            String paymentResult = moneyDomainService.initiatePayment(rechargeRecordDo);

            // 4. 通过 Converter 转换为响应DTO
            RechargeResponseDTO response = rechargeRecordConverter.toResponseDTO(rechargeRecord);
            response.setPaymentResult(paymentResult); // 设置支付结果


            // 5. 通过 Converter 转换为响应DTO
            return response;

        } catch (Exception e) {
            log.error("充值处理异常，用户ID: {}, 金额: {}",
                    request.getUserId(), request.getAmount(), e);
            throw MoneyException.moneyRechargeError(request.getUserId());
        }
    }

    @Override
    public RechargeResponseDTO getRechargeRecord(Long rechargeId) {
        log.info("查询充值记录，ID: {}", rechargeId);

        RechargeRecordDo record = rechargeRecordRepository.findById(rechargeId);
        if (record == null) {
            log.warn("充值记录不存在，ID: {}", rechargeId);
            return null;
        }

        return rechargeRecordConverter.toResponseDTO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(String orderNo, String transactionId) {
        // 1. 调用领域服务处理业务逻辑（返回结果，不发布事件）
        MoneyDomainService.PaymentCallbackResult result =
                moneyDomainService.handlePaymentSuccess(orderNo, transactionId);

        if (result == null) {
            log.warn("支付回调已处理，无需发布事件，订单号: {}", orderNo);
            return;
        }

        // 2. 在应用层发布事件（注册事务同步回调）
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        eventPublisher.publishEvent(
                                new PaymentSuccessEvent(
                                        result.getRechargeRecord(),
                                        result.getTransactionIdOrFailureReason()
                                )
                        );
                        log.info("事务提交成功，发布支付成功事件，订单号: {}", orderNo);
                    }
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentFailure(String orderNo, String failureReason) {
        // 1. 调用领域服务处理业务逻辑
        MoneyDomainService.PaymentCallbackResult result =
                moneyDomainService.handlePaymentFailure(orderNo, failureReason);

        if (result == null) {
            log.warn("支付失败回调已处理，无需发布事件，订单号: {}", orderNo);
            return;
        }

        // 2. 在应用层发布事件（注册事务同步回调）
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        eventPublisher.publishEvent(
                                new PaymentFailureEvent(
                                        result.getRechargeRecord(),
                                        result.getTransactionIdOrFailureReason()
                                )
                        );
                        log.info("事务提交成功，发布支付失败事件，订单号: {}", orderNo);
                    }
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long userId, BigDecimal amount, String description) {
//        // 强制走主库（避免主从延迟）
//        DataSourceContextHolder.setMaster();
//        // ========== 第一层防护：设置数据源路由 ==========
//        String dataSourceKey = dataSourceRouter.getDataSourceKey(userId);
//        DynamicRoutingDataSource.setDataSourceKey(dataSourceKey);
        log.info("开始处理扣款请求，用户ID: {}, 金额: {}, 描述: {}", userId, amount, description);

        // ========== 第一层防护：Redisson 分布式锁（应用层负责并发控制）==========
        String lockKey = "money:deduct:lock:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁自动续期
            boolean locked = lock.tryLock(3, TimeUnit.SECONDS);

            if (!locked) {
                log.warn("用户账户正在处理其他扣款请求，用户ID: {}", userId);
                throw new IllegalStateException("账户操作繁忙，请稍后重试");
            }

            // ========== 第二层防护：调用领域服务执行业务逻辑 ==========
            moneyDomainService.deductAccount(userId, amount, description);

            log.info("扣款处理成功，用户ID: {}, 金额: {}", userId, amount);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断，用户ID: {}", userId, e);
            throw new RuntimeException("获取锁失败", e);
        } finally {
            // 释放锁（只有当前线程持有的锁才能释放）
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁，用户ID: {}", userId);
            }
        }
    }
}
