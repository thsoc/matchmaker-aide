package com.springcloud.aide.adapter.feign;




import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 订单服务Feign客户端
 */
@FeignClient(name = "order-service", path = "/order")
public interface OrderFeignClient {

    /**
     * 创建订单
     * @param userId 用户ID
     * @param orderType 订单类型：1-会员购买
     * @param amount 金额
     * @param description 描述
     * @return 订单ID
     */
    @PostMapping("/createOrder")
    Long createOrder(@RequestParam("userId") Long userId,
                     @RequestParam("orderType") Integer orderType,
                     @RequestParam("amount") BigDecimal amount,
                     @RequestParam("description") String description);
}

