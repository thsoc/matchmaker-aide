//package com.aide.domain.service;
//
//import com.aide.common.Result.Result;
//import com.aide.infrastructure.remote.service.RemoteOrderService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//
///**
// * @author mazg
// * @description 远程订单领域类
// * @date 2026/6/9
// * @date 18:05
// */
//@Service
//@RequiredArgsConstructor
//public class OrderDomainService {
//    private final RemoteOrderService remoteOrderService;
//
//    /**
//     * @author mazg
//     * @description 创建订单
//     * @date 18:08 2026/6/9
//     * @return
//     **/
//    public Result createOrder(Long userId, Integer orderType, BigDecimal amount, String description) {
//        return remoteOrderService.createOrder(userId, orderType, amount, description);
//    }
//}
