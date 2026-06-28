package com.aide.infrastructure.remote.feign;


import com.aide.common.dto.feign.points.AddPointsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 积分服务Feign客户端
 */
@FeignClient(name = "points-service", path = "/points")
public interface PointsFeignClient {

    /**
     * 增加积分
     */
    @PostMapping("/addPoints")
    void addPoints(@RequestBody AddPointsRequest build);
}
