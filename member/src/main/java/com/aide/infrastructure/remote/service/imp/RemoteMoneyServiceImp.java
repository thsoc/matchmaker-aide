//package com.aide.infrastructure.remote.service.imp;
//
//import com.aide.common.Result.Result;
//import com.aide.common.dto.money.DeductRequest;
//import com.aide.infrastructure.remote.feign.MoneyFeignClient;
//import com.aide.infrastructure.remote.service.RemoteMoneyService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//
///**
// * @author mazg
// * @description 远程金额服务实现类
// * @date 2026/6/9
// * @date 17:46
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class RemoteMoneyServiceImp implements RemoteMoneyService {
//
//    private final MoneyFeignClient moneyFeignClient;
//
//    @Override
//    public Result deduct(Long userId, BigDecimal amount, String description) {
//        log.info("远程调用金额服务开始处理扣款请求，用户ID: {}, 金额: {}, 描述: {}", userId, amount, description);
//        DeductRequest build = DeductRequest.builder().userId(userId).amount(amount).description(description).build();
//        return moneyFeignClient.deduct(build);
//    }
//}
