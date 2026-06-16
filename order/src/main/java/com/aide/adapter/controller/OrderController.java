package com.aide.adapter.controller;

import com.aide.adapter.VO.OrderRequest;
import com.aide.common.Result.Result;
import com.aide.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

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
    @PostMapping("/createOrder")
    public Result<String> buyMember(@Valid @RequestBody OrderRequest request) {
        String result = orderService.createOrder(request);
        return Result.success(result);
    }
}
