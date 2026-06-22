package com.aide.domain.service;

import com.aide.adapter.dto.UserCouponRequest;
import com.aide.domain.model.UserCouponDo;
import com.aide.domain.model.strategy.UserCouponConverterFactory;
import com.aide.domain.repository.UserCouponRepository;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 用户优惠券领域类
 * @date 2026/6/9
 * @date 18:05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponDomainService {
    private final UserCouponRepository userCouponRepository;


    public IPage<UserCouponDo> getPageUserCoupon(Page<UserCoupon> objectPage, UserCouponDo userCouponDo, Long userId) {
        log.info(">>> getPageUserCoupon START userId={}, request={}", userId, userCouponDo);
        IPage<UserCouponDo> pageUserCoupon = userCouponRepository.getPageUserCoupon(objectPage,userCouponDo, userId);
        log.info(">>> getPageUserCoupon END userId={}, request={}, response={}", userId, userCouponDo, pageUserCoupon);
        return pageUserCoupon;
    }

    public UserCouponDo createUserCouponDo(UserCouponConverterFactory userCouponConverterFactory, UserCouponRequest userCouponRequest, Long userId) {
        UserCouponDo.injectFactory(userCouponConverterFactory);
        UserCouponDo userCouponDo = UserCouponDo.createFromDTO(userCouponRequest, userId);
        return userCouponDo;
    }
}
