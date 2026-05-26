package com.aide.service.impl;


import com.aide.common.auth.context.UserContext;
import com.aide.domain.model.SmsCodeDo;
import com.aide.domain.service.SmsCodeDomainService;
import com.aide.infrastructure.sms.SmsSender;
import com.aide.service.SmsCodeService;
import com.aide.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 短信验证码服务实现类
 * @date 2026/5/26
 * @date 12:41
 */

@Slf4j
@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    private final SmsCodeDomainService smsCodeDomainService;
    private final SmsSender smsSender;
    private final UserService userService;

    public SmsCodeServiceImpl(SmsCodeDomainService smsCodeDomainService,
                              SmsSender smsSender,
                              UserService userService) {
        this.smsCodeDomainService = smsCodeDomainService;
        this.smsSender = smsSender;
        this.userService = userService;
    }

    @Override
    public void getSmsCode(String mobile, String ipAddress) {
        SmsCodeDo smsCode = smsCodeDomainService.generateSmsCode(mobile, ipAddress);

        log.info("短信验证码已生成，手机号: {}, IP: {}", mobile, ipAddress);

        smsSender.send(mobile, smsCode.getCode());

        log.info("短信验证码已发送，手机号: {}", mobile);
    }

    @Override
    public void verifySmsCode(String mobile, String code) {
        smsCodeDomainService.verifySmsCode(mobile, code);
        log.info("短信验证码验证成功，手机号: {}", mobile);
    }

    @Override
    public void bindMobile(String mobile, String code, String ipAddress) {
        Long userId = UserContext.getUser().getId();
        verifySmsCode(mobile, code);
        //更新或添加用户手机号
        userService.updateMobile(userId, mobile);

        log.info("绑定手机号成功，用户ID: {}, 手机号: {}", userId, mobile);
    }
}
