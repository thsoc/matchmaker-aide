//package com.aide.infrastructure.remote.feign;
//
//
//import com.aide.common.Result.Result;
//import com.aide.common.dto.money.DeductRequest;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
///**
// * 资金服务Feign客户端
// */
//@FeignClient(name = "money-service", path = "/money")
//public interface MoneyFeignClient {
//
//    /**
//     * 扣款
//     */
//    @PostMapping("/deduct")
//    Result deduct(@RequestBody DeductRequest build);
//}
