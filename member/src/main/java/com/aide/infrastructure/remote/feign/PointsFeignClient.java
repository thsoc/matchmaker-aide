//package com.aide.infrastructure.remote.feign;
//
//
//import com.aide.common.dto.points.AddPointsRequest;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
///**
// * 积分服务Feign客户端
// */
//@FeignClient(name = "points-service", path = "/points")
//public interface PointsFeignClient {
//
//    /**
//     * 增加积分
//     */
//    @PostMapping("/addPoints")
//    void addPoints(@RequestBody AddPointsRequest build);
//}
