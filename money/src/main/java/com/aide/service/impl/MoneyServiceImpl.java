package com.aide.service.impl;


import com.aide.adapter.converter.RechargeRecordConverter;
import com.aide.adapter.dto.RechargeRequestDTO;
import com.aide.adapter.dto.RechargeResponseDTO;
import com.aide.domain.model.MoneyDo;
import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.repository.MoneyRepository;
import com.aide.domain.repository.RechargeRecordRepository;
import com.aide.domain.service.MoneyDomainService;
import com.aide.service.MoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public RechargeResponseDTO recharge(RechargeRequestDTO request) {
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
            moneyDomainService.saveRechargeRecord(rechargeRecord);

            // 3. 发起第三方支付（调用微信/支付宝）
            String paymentResult = moneyDomainService.initiatePayment(rechargeRecord);

            // 4. 通过 Converter 转换为响应DTO
            RechargeResponseDTO response = rechargeRecordConverter.toResponseDTO(rechargeRecord);
            response.setPaymentResult(paymentResult); // 设置支付结果


            // 5. 通过 Converter 转换为响应DTO
            return response;

        } catch (Exception e) {
            log.error("充值处理异常，用户ID: {}, 金额: {}",
                    request.getUserId(), request.getAmount(), e);
            throw new RuntimeException("充值失败: " + e.getMessage(), e);
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
        log.info("应用服务层：开始处理支付回调，订单号: {}, 交易号: {}", orderNo, transactionId);

        try {
            // 调用领域服务处理支付回调
            moneyDomainService.handlePaymentCallback(orderNo, transactionId);

            log.info("应用服务层：支付回调处理成功，订单号: {}", orderNo);

        } catch (Exception e) {
            log.error("应用服务层：支付回调处理异常，订单号: {}", orderNo, e);
            throw new RuntimeException("支付回调处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentFailure(String orderNo, String failureReason) {
        log.info("应用服务层：开始处理支付失败回调，订单号: {}, 原因: {}", orderNo, failureReason);

        try {
            // 调用领域服务处理支付失败
            moneyDomainService.handlePaymentFailure(orderNo, failureReason);

            log.info("应用服务层：支付失败回调处理完成，订单号: {}", orderNo);

        } catch (Exception e) {
            log.error("应用服务层：支付失败回调处理异常，订单号: {}", orderNo, e);
            throw new RuntimeException("支付失败回调处理失败: " + e.getMessage(), e);
        }
    }
}
