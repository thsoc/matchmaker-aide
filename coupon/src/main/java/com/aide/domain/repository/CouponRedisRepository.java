package com.aide.domain.repository;


/**
 * @author mazg
 * @description 购买优惠券
 * @date 13:28 2026/6/14
 * @return 
 **/
public interface CouponRedisRepository {
    String deduceCoupon(Long id, Long userId);

    void preheatCoupon(int advanceTime) throws Exception;
}
