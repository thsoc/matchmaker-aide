package com.aide.service.impl;

import com.aide.common.Result.Result;
import com.aide.common.auth.context.UserContext;
import com.aide.common.constant.coupon.Constant;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.common.dto.feign.order.OrderRequest;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.domain.model.OrderDo;
import com.aide.domain.service.OrderDomainService;
import com.aide.infrastructure.remote.service.RemoteCouponService;
import com.aide.service.OrderServiceStrategy;
import io.seata.core.context.RootContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mazg
 * @description 秒杀优惠券
 * @date 2026/6/28
 * @date 12:57
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderBuyCouponImp implements OrderServiceStrategy {
    private final OrderDomainService orderDomainService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RemoteCouponService remoteCouponService;

    @Transactional
    @Override
    public String createOrder(OrderRequest request) {
        log.info(">>> createOrder START xid={}", RootContext.getXID());
        if(request.getUserId() == null){ //兼容消息队列
            request.setUserId(UserContext.getUser().getId());
        }

        //获取优惠券金额,简单点从redis获取,也可以去其他服务查询
        log.info("获取优惠券金额，用户ID: {}", request.getDescription());
        CouponInfo couponInfo = (CouponInfo)redisTemplate.opsForValue().get(Constant.COUPON_MONEY_KEY_PREFIX + request.getId());
        log.info("优惠券金额: {}", couponInfo.getAmount());

        //下单
        log.info("创建订单，用户ID: {}, 订单类型: {}, 金额: {}, 描述: {}", request.getUserId(), OrderTypeEnum.BUY_MEMBER.getCode(), couponInfo.getAmount(), request.getDescription());
        //todo 应该有个订单详情保存优惠券id，这边放在描述中
        String orderNo = orderDomainService.createOrder(request.getUserId(), OrderTypeEnum.BUY_MEMBER.getCode(), couponInfo.getAmount(), request.getId().toString());
        log.info("下单成功，订单编号: {}", orderNo);
        return orderNo;
    }

    @Override
    public OrderTypeEnum getOrderType() {
        return OrderTypeEnum.BUY_COUPON;
    }

    @Override
    public void nextStep(OrderDo orderDo) {
        //创建用户优惠券，//todo 这一步可以发MQ，使用orderNo保证幂等性(要在会员服务中新增会员购买记录表)
        log.info("创建用户优惠券，用户ID: {}, 金额: {}", UserContext.getUserId(), orderDo.getAmount());
        //todo 应该有个订单详情保存优惠券id，这边放在描述中
        Result result = remoteCouponService.buyCouPon(Long.valueOf(orderDo.getDescription()));
        log.info("创建用户优惠券，用户ID: {}",  UserContext.getUserId());
        if (!result.isSuccess()){
            log.error("创建用户优惠券，用户ID: {}", UserContext.getUserId());
            throw new RuntimeException("创建用户优惠券");
        }
    }
}
