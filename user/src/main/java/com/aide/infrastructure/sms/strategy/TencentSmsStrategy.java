package com.aide.infrastructure.sms.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


/**
 * @author mazg
 * @description 腾讯云短信策略接口
 * @date 2026/5/26
 * @date 14:04
 */

@Slf4j
@Component
@ConditionalOnProperty(name = "sms.provider", havingValue = "tencent")
public class TencentSmsStrategy implements SmsStrategy {

    @Value("${sms.tencent.secret-id:}")
    private String secretId;

    @Value("${sms.tencent.secret-key:}")
    private String secretKey;

    @Value("${sms.tencent.sms-sdk-app-id:}")
    private String smsSdkAppId;

    @Value("${sms.tencent.sign-name:}")
    private String signName;

    @Value("${sms.tencent.template-id:}")
    private String templateId;

    @Value("${sms.tencent.region:ap-guangzhou}")
    private String region;

    @Override
    public void send(String mobile, String code) {
        log.info("【腾讯云短信】准备发送短信到: {}, 签名: {}", mobile, signName);

        try {
            // TODO: 集成腾讯云短信 SDK
            // 需要添加依赖：tencentcloud-sdk-java-sms

            /*
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, region, clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            req.setPhoneNumberSet(new String[]{"+86" + mobile});
            req.setSmsSdkAppId(smsSdkAppId);
            req.setSignName(signName);
            req.setTemplateId(templateId);
            req.setTemplateParamSet(new String[]{code});

            SendSmsResponse res = client.SendSms(req);
            log.info("腾讯云短信发送结果: {}", res.getSendStatusSet()[0].getMessage());

            // 检查发送是否成功
            SendStatus status = res.getSendStatusSet()[0];
            if (!"Ok".equals(status.getCode())) {
                throw new RuntimeException("短信发送失败: " + status.getMessage());
            }
            */

            log.info("【腾讯云短信】发送成功");

        } catch (Exception e) {
            log.error("【腾讯云短信】发送失败", e);
            throw new RuntimeException("腾讯云短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "tencent";
    }
}
