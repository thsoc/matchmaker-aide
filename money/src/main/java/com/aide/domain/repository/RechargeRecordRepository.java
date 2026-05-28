package com.aide.domain.repository;

import com.aide.domain.model.RechargeRecordDo;

/**
 * @author mazg
 * @description 充值记录仓储接口 - 定义充值记录的持久化契约
 *
 * 职责：
 * 1. 提供充值记录的查询和保存能力
 * 2. 隐藏基础设施层的实现细节
 *
 * @date 2026/5/28
 */
public interface RechargeRecordRepository {

    /**
     * 保存充值记录（新增或更新）
     *
     * @param rechargeRecord 充值记录领域对象
     */
    void save(RechargeRecordDo rechargeRecord);

    /**
     * 根据订单号查询充值记录
     *
     * @param orderNo 订单号
     * @return 充值记录领域对象，不存在时返回 null
     */
    RechargeRecordDo findByOrderNo(String orderNo);

    /**
     * 根据 ID 查询充值记录
     *
     * @param id 记录 ID
     * @return 充值记录领域对象，不存在时返回 null
     */
    RechargeRecordDo findById(Long id);

    /**
     * 原子性更新充值记录状态为成功
     *
     * 使用数据库层面的原子操作防止并发问题：
     * UPDATE recharge_record
     * SET status = 2, payment_result = ?, update_time = NOW()
     * WHERE order_no = ? AND status != 2
     *
     * @param orderNo 订单号
     * @param transactionId 交易号
     * @return 影响的行数，0表示已被其他请求处理
     */
    int updateStatusToSuccess(String orderNo, String transactionId);

    /**
     * 原子性更新充值记录状态为失败
     *
     * @param orderNo 订单号
     * @param failureReason 失败原因
     * @return 影响的行数，0表示已被其他请求处理
     */
    int updateStatusToFailure(String orderNo, String failureReason);
}
