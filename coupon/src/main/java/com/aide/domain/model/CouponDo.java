package com.aide.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 优惠券领域对象
 * @date 2026/6/14
 * @date 16:20
 */
@Data
@Builder
@Getter
public class CouponDo {
    /**
     * 优惠券ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;


    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券生效时间
     */
    private LocalDateTime effectiveTime;

    /**
     * 优惠券失效时间
     */
    private LocalDateTime expireTime;


    /**
     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
     */
    private Integer couponDiscountType;
    /**
     * 发行总量
     */
    private Integer totalCount;

    /**
     * 优惠券剩余数量
     */
    private Integer availableStock ;


    /**
     * 对于代金券，直接存储固定抵扣金额（如 20 元）；对于折扣券，存储折扣比例（如 0.85 代表 85 折）
     */
    private BigDecimal amount;

    /**
     * 使用门槛。满减券和折扣券需要填写（如满 100 可用），代金券如果无门槛则填 0。
     */
    private BigDecimal conditionAmount ;

    /**
     * 折扣上限
     */
    private BigDecimal maxDiscount;

    /**
     * 规则json
     */
    private String ruleJson;



    /**
     * 描述
     */
    private String description;
    /**
     *
     */
    private String status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;
    /**
     * 版本号
     */
    private String version;

    /**
     * 校验优惠券信息
     */
    public void validateCoupon() {
        if (couponName == null){
            throw new RuntimeException("优惠券名称不能为空");
        }
        if (effectiveTime == null){
            throw new RuntimeException("优惠券生效时间不能为空");
        }
        if (expireTime == null){
            throw new RuntimeException("优惠券失效时间不能为空");
        }
        if (couponDiscountType == null){
            throw new RuntimeException("优惠券折扣方式不能为空");
        }
        if (totalCount == null){
            throw new RuntimeException("发行总量不能为空");
        }
        if (amount == null){
            throw new RuntimeException("优惠券金额不能为空");
        }
        if (conditionAmount == null){
            throw new RuntimeException("使用门槛不能为空");
        }
        if (maxDiscount == null){
            throw new RuntimeException("折扣上限不能为空");
        }
    }

    /**
     * 初始化优惠券信息
     */
    public void initCoupon(Long userId) {
        this.createBy = userId.toString();
        this.updateBy = userId.toString();
        this.createTime = LocalDateTime.now();
        this.status = "1";
        this.remark = "创建优惠券";
    }
}
