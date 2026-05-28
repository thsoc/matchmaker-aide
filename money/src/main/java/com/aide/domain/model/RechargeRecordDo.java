package com.aide.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author mazg
 * @description 充值记录领域对象
 * @date 2026/5/25
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRecordDo {
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 充值状态：0-待支付 1-充值中 2-成功 3-失败
     */
    private Integer status;

    /**
     * 充值订单号
     */
    private String orderNo;

    /**
     * 支付方式：1-微信 2-支付宝 3-银行卡
     */
    private Integer payType;

    /**
     * 充值时间
     */
    private LocalDateTime rechargeTime;

    /**
     * 支付结果（预支付ID或支付表单）
     */
    private String paymentResult;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;









    // ==================== 业务规则方法 ====================


    /**
     * 标记充值中（业务规则）
     */
    public void markAsProcessing() {
        if (this.status == null || this.status == 2) {
            throw new IllegalStateException("当前状态不允许修改");
        }
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }
    /**
     * 标记充值成功（业务规则）
     *
     * 规则：
     * 1. 只有"充值中"的状态才能标记为成功
     * 2. 成功后需要更新时间和状态
     */
    public void markAsSuccess() {
        if (this.status == null || this.status != 1) {
            throw new IllegalStateException("只有充值中的记录才能标记为成功");
        }
        this.status = 2;
        this.updateTime = LocalDateTime.now();
    }


    /**
     * 标记充值失败（业务规则）
     *
     * 规则：
     * 1. 只有"充值中"或"待支付"的状态才能标记为失败
     * 2. 失败原因记录到备注中
     */
    public void markAsFailure(String reason) {
        if (this.status == null || (this.status != 0 && this.status != 1)) {
            throw new IllegalStateException("只有待支付或充值中的记录才能标记为失败");
        }
        this.status = 3;
        this.remark = reason;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 验证充值金额是否有效（业务规则）
     */
    public void validateAmount() {
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        if (this.amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new IllegalArgumentException("单笔充值不能超过10000元");
        }
    }

    /**
     * 判断是否可以取消（业务规则）
     */
    public boolean canCancel() {
        return this.status != null && (this.status == 0 || this.status == 1);
    }

    /**
     * 标记为已取消
     */
    public void cancel() {
        if (!canCancel()) {
            throw new IllegalStateException("当前状态不允许取消");
        }
        this.status = 4; // 4-已取消
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 标记充值是否成功
     */
    public boolean isSuccess() {
        return status != null && status == 2;
    }

    /**
     * 标记充值是否失败
     */
    public boolean isFailed() {
        return status != null && status == 3;
    }

    /**
     * 设置默认值（工厂方法）
     *
     * 业务规则：
     * 1. 初始状态为"充值中"
     * 2. 自动生成订单号
     * 3. 自动设置时间
     */
    public static RechargeRecordDo create(Long userId, BigDecimal amount, Integer payType, String remark) {
        RechargeRecordDo record = new RechargeRecordDo();
        record.userId = userId;
        record.amount = amount;
        record.payType = payType;
        record.remark = remark;

        // 设置默认值
        record.status = 1; // 默认充值中
        record.orderNo = generateOrderNo(); // 自动生成订单号
        record.rechargeTime = LocalDateTime.now();
        record.createTime = LocalDateTime.now();
        record.updateTime = LocalDateTime.now();

        // 验证业务规则
        record.validateAmount();

        return record;
    }

    /**
     * 生成充值订单号（领域规则）
     * 格式：RC + 时间戳(yyyyMMddHHmmss) + UUID前8位
     */
    private static String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "RC" + timestamp + uuid.toUpperCase();
    }
}
