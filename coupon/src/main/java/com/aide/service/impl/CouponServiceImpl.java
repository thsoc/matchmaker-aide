package com.aide.service.impl;

import com.aide.adapter.VO.CouponVo;
import com.aide.adapter.VO.UserCouponVo;
import com.aide.adapter.dto.CouponRequest;
import com.aide.adapter.dto.UserCouponRequest;
import com.aide.common.auth.context.UserContext;
import com.aide.common.domain.IClock;
import com.aide.common.domain.SystemClock;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.common.util.PageUtil;
import com.aide.domain.model.CouponDo;
import com.aide.domain.model.UserCouponDo;
import com.aide.domain.repository.CouponRedisRepository;
import com.aide.domain.repository.CouponRepository;
import com.aide.domain.service.CouponDomainService;
import com.aide.domain.service.UserCouponDomainService;
import com.aide.domain.event.ReceiveCouponEvent;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.aide.service.CouponService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description 优惠券服务实现类
 * @date 2026/5/29
 * @date 11:32
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponDomainService couponDomainService;
    private final UserCouponDomainService userCouponDomainService;
    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CouponRedisRepository couponRedisRepository;

    @Override
    @Transactional
    public String createCoupon(CouponRequest request) {
        Long userId = UserContext.getUser().getId();
        //打印request所有字段信息
        log.info(">>> createCoupon START userid={}, request={}", userId, request);
        //获取优惠券领域对象
        IClock clock = new SystemClock();
        CouponDo couponDo = couponDomainService.createCouponDo(request, userId, null, clock);

        return couponRepository.createCoupon(couponDo);
    }

    @Override
    public Page<UserCouponVo> getPageUserCoupon(UserCouponRequest userCouponRequest) {
        Long userId = UserContext.getUserId();
        log.info(">>> getPageUserCoupon START userId={}, request={}", UserContext.getUserId(), userCouponRequest);
        //将用户优惠券请求对象转换领域对象
        UserCouponDo userCouponDo = userCouponDomainService.createUserCouponDo(userCouponRequest, userId);
        Page<UserCoupon> objectPage = PageUtil.buildPage(userCouponRequest);
        IPage<UserCouponDo> pageUserCoupon = userCouponDomainService.getPageUserCoupon(objectPage, userCouponDo, userId);

        //根据需要转换返回对象（加密等）

        List<UserCouponVo> collect = pageUserCoupon.getRecords().stream().map(item -> getVoFromUserCoupon(item)).collect(Collectors.toList());
        Page<UserCouponVo> resultPage = new Page<>(pageUserCoupon.getCurrent(), pageUserCoupon.getSize(), pageUserCoupon.getTotal());
        resultPage.setRecords(collect);
        return resultPage;
    }

    @Override
    public Page<CouponVo> getPageCoupon(CouponRequest request) {
        log.info(">>> getPageCoupon START request={}", request);
        //获取优惠券领域对象
        Page<CouponDo> pageCoupon = couponDomainService.getPageCoupon(request);
        List<CouponVo> collect = pageCoupon.getRecords().stream().map(couponDo -> getVoFromCoupon(couponDo)).collect(Collectors.toList());
        Page<CouponVo> resultPage = new Page<>(pageCoupon.getCurrent(), pageCoupon.getSize(), pageCoupon.getTotal());
        resultPage.setRecords(collect);
        //根据需要转换返回对象（加密等）
        return resultPage;
    }

    /**
     * @author mazg
     * @description
     * * 1.一人一单，
     *      * 2.库存-1
     *      * 3.异步下单待支付
     *      * 4.取消支付，更新redis中的库存,异步MQ删除订单，
     *      * 5.余额支付：确认支付，更新redis中的库存 ,异步MQ更新订单状态+扣款
     *      * 6.支付宝等支付：异步回调确认支付，更新redis中的库存 ,异步MQ更新订单状态
     * @date 15:10 2026/6/23
     * @return
     **/
    @Override
    public void receiveCoupon(Long id) {
        Long userId = UserContext.getUserId();
        log.info(">>> receiveCoupon START userId={}, couponId={}", userId, id);
        //1.一人一单， 2.库存-1
        couponDomainService.deduceCoupon(id, userId);

        //3.异步下单待支付，发布领域事件
        eventPublisher.publishEvent(new ReceiveCouponEvent(this, id,userId));

    }


    /**
     * @author mazg
     * @description 预热快生效的优惠券，保存到redis
     * @date 20:31 2026/6/24
     * @return
     **/
    @Override
    public void preheatCoupon(String param) {
        //提前时间（分钟）
        int advanceTime = 30;
        if(StringUtils.isNotBlank(param)){
            advanceTime = Integer.parseInt(param);
        }
        couponRedisRepository.preheatCoupon(advanceTime);
    }

    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo info = couponDomainService.getCouponInfo(id);
        return info;
    }


    private CouponVo getVoFromCoupon(CouponDo entity){
        return CouponVo.builder()
                .amount(entity.getCouponRule().getAmount())
                .couponDiscountType(entity.getCouponRule().getDiscountType().getCode())
                .couponName(entity.getCouponName())
                .conditionAmount(entity.getCouponRule().getConditionAmount())
                .description(entity.getCouponQuota().getDescription())
                .effectiveTime(entity.getCouponRule().getEffectiveTime())
                .expireTime(entity.getCouponRule().getExpireTime())
                .id(entity.getId())
                .maxDiscount(entity.getCouponRule().getMaxDiscount())
                .ruleJson(entity.getCouponRule().getRuleJson())
                .status(entity.getCouponRule().getStatus())
                .remark(entity.getCouponQuota().getRemark())
                .build();
    }

    private UserCouponVo getVoFromUserCoupon(UserCouponDo entity){
        return UserCouponVo.builder()
                .amount(entity.getAmount())
                .buyTime(entity.getBuyTime())
                .couponDiscountType(entity.getCouponDiscountType())
                .couponId(entity.getCouponId())
                .couponName(entity.getCouponName())
                .conditionAmount(entity.getConditionAmount())
                .createTime(entity.getCreateTime())
                .description(entity.getDescription())
                .effectiveTime(entity.getEffectiveTime())
                .expireTime(entity.getExpireTime())
                .id(entity.getId())
                .maxDiscount(entity.getMaxDiscount())
                .orderNo(entity.getOrderNo())
                .ruleJson(entity.getRuleJson())
                .status(entity.getStatus())
                .updateTime(entity.getUpdateTime())
                .userId(entity.getUserId())
                .deleteTime(entity.getDeleteTime())
                .remark(entity.getRemark())
                .build();
    }

}
