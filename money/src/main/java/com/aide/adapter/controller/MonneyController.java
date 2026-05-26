package com.aide.adapter.controller;

import com.aide.common.Result.Result;
import com.aide.service.MoneyService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/money") //
public class MonneyController {

    private final MoneyService moneyService;

    public MonneyController(MoneyService moneyService) {
        this.moneyService = moneyService;
    }

    /**
     * 获取用户金额
     */
    @RequestMapping("/getMoney/{account}")
    public Result getMoney(@PathVariable("account") String account) {
        if (account == null || account.trim().isEmpty()) {
            return Result.error("账户不能为空");
        }
        return Result.success(moneyService.getMoney(account));
    }

}
