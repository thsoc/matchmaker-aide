package com.aide.infrastructure.remote.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 资金服务Feign客户端
 */
@FeignClient(name = "money-service", path = "/money")
public interface MoneyFeignClient {

    /**
     * 扣款
     * @param userId 用户ID
     * @param amount 金额
     * @param description 描述
     */
    @PostMapping("/deduct")
    void deduct(@RequestParam("userId") Long userId,
                @RequestParam("amount") BigDecimal amount,
                @RequestParam("description") String description);
}
