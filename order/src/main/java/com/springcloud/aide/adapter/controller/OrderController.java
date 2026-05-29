package com.springcloud.aide.adapter.controller;

import com.springcloud.aide.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mazg
 * @description 会员控制器
 * @date 2026/5/29
 * @date 11:30
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService memberService;

    /**
     * 购买会员
     */
    @RequestMapping("/buyMember/{userId}")
    public String buyMember(@PathVariable String userId) {
//        return memberService.buyMember(userId);
        return "购买会员成功";
    }
}
