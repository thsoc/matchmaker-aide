package com.aide.infrastructure.converter;

import com.aide.domain.model.CouponDo;
import com.aide.domain.model.UserCouponDo;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description
 * @date 2026/6/23
 * @date 04:06
 */
@Component
public class UserCouponConverter {

    /**
     * 将 PO 转换为 DO
     */
    public Page<UserCouponDo> convertCouponPage(Page<UserCoupon> pojoPage) {
        List<UserCouponDo> couponDos = pojoPage.getRecords().stream()
                .map(UserCouponDo::fromPOJO)
                .collect(Collectors.toList());
        Page<UserCouponDo> resultPage = new Page<>(pojoPage.getCurrent(), pojoPage.getSize(), pojoPage.getTotal());
        resultPage.setRecords(couponDos);
        // 2. 保留原始分页元数据（总记录数、页码等）
        return resultPage;
    }
}