package com.aide.infrastructure.persistence.mapper;

import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mazg
 * @description 用户优惠券 MAP
 * @date 2026/6/14
 * @date 16:39
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
}
