package com.aide.adapter.controller;

import com.aide.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.aide.common.Result.Result;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 会员控制器
 * @date 2026/5/29
 * @date 11:30
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 购买会员
     * @param userId 用户ID
     * @param memberType 会员类型：1-普通会员 2-高级会员 3-VIP会员
     */
    @PostMapping("/buyMember")
    public Result<String> buyMember(@RequestParam("userId") Long userId,
                                    @RequestParam("memberType") Integer memberType) {
        if (userId == null || userId <= 0) {
            return Result.error("用户ID无效");
        }
        if (memberType == null || memberType < 1 || memberType > 3) {
            return Result.error("会员类型无效，请选择1-3");
        }

        String result = memberService.buyMember(userId, memberType);
        return Result.success(result);
    }

    /**
     * 获取会员不同类型的价格信息
     */
    @GetMapping("/getMemberPrice/{memberType}")
    public Result<BigDecimal> getMemberPrice(@PathVariable("memberType") Integer memberType) {
        BigDecimal result = memberService.getMemberPrice(memberType);
        return Result.success(result);
    }

}
