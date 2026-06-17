package com.aide.infrastructure.remote.feign;




import com.aide.common.Result.Result;
import com.aide.common.dto.order.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 订单服务Feign客户端
 */
@FeignClient(name = "order-service", path = "/order")
public interface OrderFeignClient {

    /**
     * 创建订单
     * @return 订单ID
     */
    @PostMapping("/createOrder")
    Result createOrder(@RequestBody OrderRequest build);
}

