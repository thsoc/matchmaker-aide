package com.aide.adapter.controller;

import com.aide.common.aspect.Idempotent;
import com.aide.common.dto.order.OrderRequest;
import com.aide.common.Result.Result;
import com.aide.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author mazg
 * @description 会员控制器
 * @date 2026/5/29
 * @date 11:30
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 创建订单
     */
    // 使用前端传来的幂等键作为防重标识
    @Idempotent(key = "#createOrderReq.userId + '_' + createOrderReq.orderType + '_' + createOrderReq.memberType", expire = 30)
    @PostMapping("/createOrder")
    public Result<String> buyMember(@Valid @RequestBody OrderRequest request) {
        String result = orderService.createOrder(request);
        return Result.success(result);
    }
}
