package com.aide.domain.model;

import com.aide.adapter.dto.CouponRequest;
import com.aide.common.domain.IClock;
import com.aide.domain.model.strategy.CouponConverterFactory;
import com.aide.domain.model.strategy.CouponConverterStrategy;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 优惠券领域对象
 * @date 2026/6/14
 * @date 16:20
 */
@Getter
@Builder(toBuilder = true)
public class CouponDo {
    /**
     * 优惠券ID
     */
    private  Long id;


    /**
     * 优惠券规则
     */
    private  CouponRule couponRule;


    /**
     * 优惠券其他聚合根
     */
    private  CouponQuota couponQuota;

    private CouponDo(){}


    private CouponDo(Long id, CouponRule couponRule, CouponQuota couponQuota) {
        this.id = id;
        this.couponRule = couponRule;
        this.couponQuota = couponQuota;
    }

    public static CouponDoBuilder rebuildBuilder() {
        return new CouponDoBuilder();
    }


    // 1. 持有一个静态的工厂引用，由外部（应用层）在启动时注入
    private static CouponConverterFactory factory;

    // 2. 提供一个静态方法，供应用层在启动时调用，完成依赖注入
    public static void injectFactory(CouponConverterFactory couponFactory) {
        CouponDo.factory = couponFactory;
    }

    /**
     * 校验优惠券信息
     */
    public void validateCoupon() {
        if (couponQuota.getCouponName() == null) {
            throw new RuntimeException("优惠券名称不能为空");
        }
        if (couponRule == null) {
            throw new RuntimeException("优惠券规则不能为空");
        }
        if (couponRule.getExpireTime() == null) {
            throw new RuntimeException("优惠券失效时间不能为空");
        }
        if (couponRule.getDiscountType() == null) {
            throw new RuntimeException("优惠券折扣方式不能为空");
        }
        if (couponRule.getTotalCount() == null) {
            throw new RuntimeException("发行总量不能为空");
        }
        if (couponRule.getAmount() == null) {
            throw new RuntimeException("优惠券金额不能为空");
        }
        if (couponRule.getConditionAmount() == null) {
            throw new RuntimeException("使用门槛不能为空");
        }
        if (couponRule.getMaxDiscount() == null) {
            throw new RuntimeException("折扣上限不能为空");
        }
    }

    // 自定义 Builder 逻辑，确保状态计算一定会被触发
    public static class CouponDoBuilder {
        private  Long id;
        private  CouponRule couponRule;
        private  CouponQuota couponQuota;

        private CouponDoBuilder() {

        }
        public CouponDoBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public CouponDoBuilder couponRule(CouponRule couponRule) {
            this.couponRule = couponRule;
            return this;
        }
        public CouponDoBuilder couponQuota(CouponQuota couponQuota) {
            this.couponQuota = couponQuota;
            return this;
        }



        public CouponDo rebuild() {
            CouponDo coupon = new CouponDo();
            coupon.id = this.id;
            coupon.couponRule = this.couponRule;
            coupon.couponQuota = this.couponQuota;

            // 在对象构建完成的瞬间，自动触发内部状态推导
            coupon.couponRule.calculateLifeCycleStatus(null);
            return coupon;
        }


    }


    public static CouponDo createFromDTO(CouponRequest request, Long userId, IClock clock, String status) {
        // 防御性编程，确保工厂已注入
        if (factory == null) {
            throw new IllegalStateException("CouponConverterFactory 未注入到 CouponDo 中！");
        }

        // 4. 委托给策略工厂去处理具体的创建逻辑
        CouponConverterStrategy strategy = factory.getConverter(request.getCouponDiscountType());
        CouponDo convert = strategy.convert(request);
        CouponDoBuilder builder = convert.toBuilder();
        if (userId != null) {
            builder.couponQuota(convert.getCouponQuota().toBuilder()
                    .createBy(userId.toString())
                    .updateBy(userId.toString()).build());
        }
        if (clock != null) {
            //将clock转为LocalDateTime
            LocalDateTime currentTime = clock.getCurrentTime();
            convert.getCouponQuota().toBuilder()
                    .createTime(currentTime).build();
        }
        if (status != null) {
            convert.getCouponRule().toBuilder()
                    .status(status).build();
        }
        if (status == null){
            convert.getCouponRule().toBuilder().status("0");
        }
        convert.getCouponQuota().toBuilder().remark("创建优惠券");
        CouponDo couponDo = builder.build();
        return couponDo;
    }

}
