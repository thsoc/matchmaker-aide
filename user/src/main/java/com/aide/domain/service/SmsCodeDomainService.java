package com.aide.domain.service;



import com.aide.domain.model.SmsCodeDo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author mazg
 * @description 短信验证码领域服务
 * @date 2026/5/26
 * @date 12:30
 */
@Component
public class SmsCodeDomainService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final String SMS_LAST_SEND_PREFIX = "sms:last_send:";

    private final long expireSeconds;
    private final long resendIntervalSeconds;

    public SmsCodeDomainService() {
        this.expireSeconds = 300;
        this.resendIntervalSeconds = 60;
    }

    public SmsCodeDo generateSmsCode(String mobile, String ipAddress) {
        checkResendLimit(mobile);

        SmsCodeDo smsCode = new SmsCodeDo(mobile, ipAddress);
        smsCode.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));

        saveToCache(smsCode);
        recordLastSendTime(mobile);

        return smsCode;
    }

    private void checkResendLimit(String mobile) {
        String lastSendKey = SMS_LAST_SEND_PREFIX + mobile;
        Object lastSendTime = redisTemplate.opsForValue().get(lastSendKey);

        if (lastSendTime != null) {
            long lastSendTimestamp = (Long) lastSendTime;
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - lastSendTimestamp) / 1000;

            if (elapsedSeconds < resendIntervalSeconds) {
                long remainingSeconds = resendIntervalSeconds - elapsedSeconds;
                throw new IllegalStateException(String.format("短信发送过于频繁，请%d秒后再试", remainingSeconds));
            }
        }
    }

    private void saveToCache(SmsCodeDo smsCode) {
        String smsCodeKey = SMS_CODE_PREFIX + smsCode.getMobile();
        redisTemplate.opsForValue().set(smsCodeKey, smsCode, expireSeconds, TimeUnit.SECONDS);
    }

    private void recordLastSendTime(String mobile) {
        String lastSendKey = SMS_LAST_SEND_PREFIX + mobile;
        redisTemplate.opsForValue().set(lastSendKey, System.currentTimeMillis(), resendIntervalSeconds, TimeUnit.SECONDS);
    }

    public SmsCodeDo getSmsCode(String mobile) {
        String smsCodeKey = SMS_CODE_PREFIX + mobile;
        Object cached = redisTemplate.opsForValue().get(smsCodeKey);

        if (cached == null) {
            throw new IllegalArgumentException("验证码不存在或已过期");
        }

        return (SmsCodeDo) cached;
    }

    public void verifySmsCode(String mobile, String inputCode) {
        SmsCodeDo smsCode = getSmsCode(mobile);
        smsCode.verify(inputCode);

        updateCache(smsCode);
    }

    private void updateCache(SmsCodeDo smsCode) {
        String smsCodeKey = SMS_CODE_PREFIX + smsCode.getMobile();
        long remainingSeconds = smsCode.getExpireTime() != null
                ? java.time.Duration.between(LocalDateTime.now(), smsCode.getExpireTime()).getSeconds()
                : expireSeconds;

        if (remainingSeconds > 0) {
            redisTemplate.opsForValue().set(smsCodeKey, smsCode, remainingSeconds, TimeUnit.SECONDS);
        }
    }
}
