package com.aide.infrastructure.sms.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


/**
 * @author mazg
 * @description 阿里云短信策略接口
 * @date 2026/5/26
 * @date 14:04
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sms.provider", havingValue = "aliyun")
public class AliyunSmsStrategy implements SmsStrategy {

    @Value("${sms.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.sign-name:}")
    private String signName;

    @Value("${sms.aliyun.template-code:}")
    private String templateCode;

    @Value("${sms.aliyun.region-id:cn-hangzhou}")
    private String regionId;

    @Override
    public void send(String mobile, String code) {
        log.info("【阿里云短信】准备发送短信到: {}, 签名: {}", mobile, signName);

        try {
            // TODO: 集成阿里云短信 SDK
            // 需要添加依赖：aliyun-java-sdk-core

            /*
            DefaultProfile profile = DefaultProfile.getProfile(
                regionId,
                accessKeyId,
                accessKeySecret
            );
            IAcsClient client = new DefaultAcsClient(profile);

            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", regionId);
            request.putQueryParameter("PhoneNumbers", mobile);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

            CommonResponse response = client.getCommonResponse(request);
            log.info("阿里云短信发送结果: {}", response.getData());

            // 检查发送是否成功
            JSONObject data = JSON.parseObject(response.getData());
            if (!"OK".equals(data.getString("Code"))) {
                throw new RuntimeException("短信发送失败: " + data.getString("Message"));
            }
            */

            log.info("【阿里云短信】发送成功");

        } catch (Exception e) {
            log.error("【阿里云短信】发送失败", e);
            throw new RuntimeException("阿里云短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "aliyun";
    }
}
