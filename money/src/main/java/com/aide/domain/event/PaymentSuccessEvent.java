package com.aide.domain.event;

import com.aide.domain.model.RechargeRecordDo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author mazg
 * @description 支付成功领域事件
 *
 * 职责：
 * 1. 封装支付成功的业务事件
 * 2. 携带充值记录信息供监听器处理
 * @date 2026/5/28
 * @date 13:52
 */
@Getter
public class PaymentSuccessEvent extends ApplicationEvent {

    private final RechargeRecordDo rechargeRecord;
    private final String transactionId; // 第三方支付交易号

    public PaymentSuccessEvent(RechargeRecordDo rechargeRecord, String transactionId) {
        super(rechargeRecord);
        this.rechargeRecord = rechargeRecord;
        this.transactionId = transactionId;
    }
}

