package com.aide.domain.event;

import com.aide.domain.model.RechargeRecordDo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author mazg
 * @description 支付失败领域事件
 *
 * 职责：
 * 1. 封装支付失败的业务事件
 * 2. 携带失败原因供监听器处理
 * @date 2026/5/28
 * @date 13:54
 */
@Getter
public class PaymentFailureEvent extends ApplicationEvent {

    private final RechargeRecordDo rechargeRecord;
    private final String failureReason;

    public PaymentFailureEvent(RechargeRecordDo rechargeRecord, String failureReason) {
        super(rechargeRecord);
        this.rechargeRecord = rechargeRecord;
        this.failureReason = failureReason;
    }
}
