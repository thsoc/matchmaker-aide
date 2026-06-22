package com.aide.domain.repository;


import com.aide.domain.model.CouponDo;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mazg
 * @description 优惠券仓储接口 - 定义领域对象的持久化契约
 * @date 13:28 2026/6/14
 * @return 
 **/
public interface CouponRepository {

    /**
     * 创建优惠券
     * @param couponDo 订单领域对象
     * @return
     */
    String createCoupon(CouponDo couponDo);

    IPage<CouponDo> getPageCoupon(Page<Coupon> objectPage, CouponDo couponDo);
}
