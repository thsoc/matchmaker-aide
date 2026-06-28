package com.aide.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mazg
 * @description
 * @date 2026/6/28
 * @date 12:09
 */
@Slf4j
@Configuration
public class RabbitConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true); // 必须设置为 true，否则 Return 不会触发

        // Confirm 回调（全局，也可在每个发送时通过 CorrelationData 单独设置）
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息未到达 Exchange, correlationId: {}, cause: {}",
                        correlationData != null ? correlationData.getId() : null, cause);
                // 此处可写入本地消息表进行补偿
            }
        });

        // Return 回调（路由失败时触发）
        template.setReturnsCallback(returned -> {
            log.error("消息路由失败, exchange: {}, routingKey: {}, replyCode: {}, replyText: {}",
                    returned.getExchange(), returned.getRoutingKey(),
                    returned.getReplyCode(), returned.getReplyText());
            // 此处可将 returned.getMessage() 保存到本地消息表，等待重发
        });

        return template;
    }
}
