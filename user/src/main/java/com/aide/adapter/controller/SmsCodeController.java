package com.aide.adapter.controller;

/**
 * @author mazg
 * @description 短信验证码控制器
 * @date 2026/5/26
 * @date 12:43
 */
import com.aide.common.Result.IpUtils;
import com.aide.common.Result.Result;
import com.aide.service.SmsCodeService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/sms")
public class SmsCodeController {

    private final SmsCodeService smsCodeService;

    public SmsCodeController(SmsCodeService smsCodeService) {
        this.smsCodeService = smsCodeService;
    }

    /**
     * 获取短信验证码
     */
    @PostMapping("/getCode")
    public Result<Void> getSmsCode(
            @RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String mobile,
            HttpServletRequest request) {
        try {
            smsCodeService.getSmsCode(mobile, IpUtils.getIpAddress(request));
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取短信验证码失败: " + e.getMessage());
        }
    }


    /**
     * 绑定手机号
     */
    @PostMapping("/bindMobile")
    public Result<Void> bindMobile(
            @RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String mobile,
            @RequestParam String code,
            HttpServletRequest request) {
        try {
            smsCodeService.bindMobile(mobile, code, IpUtils.getIpAddress(request));
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            return Result.error("绑定手机号失败: " + e.getMessage());
        }
    }

}
