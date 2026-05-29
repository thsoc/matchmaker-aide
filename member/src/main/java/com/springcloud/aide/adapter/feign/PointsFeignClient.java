package com.springcloud.aide.adapter.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 积分服务Feign客户端
 */
@FeignClient(name = "points-service", path = "/points")
public interface PointsFeignClient {

    /**
     * 增加积分
     * @param userId 用户ID
     * @param points 积分数
     * @param remark 备注
     */
    @PostMapping("/addPoints")
    void addPoints(@RequestParam("userId") Long userId,
                   @RequestParam("points") Integer points,
                   @RequestParam("remark") String remark);
}
