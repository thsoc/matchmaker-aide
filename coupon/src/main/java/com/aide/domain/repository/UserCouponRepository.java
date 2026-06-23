package com.aide.domain.repository;


import com.aide.domain.model.UserCouponDo;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mazg
 * @description 订单仓储接口 - 定义领域对象的持久化契约
 * @date 13:28 2026/6/14
 * @return 
 **/
public interface UserCouponRepository {

    Page<UserCouponDo> getPageUserCoupon(Page<UserCoupon> userCouponRequest, UserCouponDo userCouponDo, Long userId);
}
