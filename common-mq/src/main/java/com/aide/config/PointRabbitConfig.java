package com.aide.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mazg
 * @description
 * @date 2026/6/28
 * @date 12:09
 */
@Configuration
public class PointRabbitConfig {

    @Bean
    public Queue pointQueue() {
        return QueueBuilder.durable("point.queue").build();
    }

    @Bean
    public DirectExchange pointExchange() {
        return ExchangeBuilder.directExchange("point.exchange").durable(true).build();
    }

    @Bean
    public Binding pointBinding(Queue pointQueue, DirectExchange pointExchange) {
        return BindingBuilder.bind(pointQueue).to(pointExchange).with("point.routingKey");
    }

    // 死信队列
    @Bean
    public Queue pointDlq() {
        return QueueBuilder.durable("point.queue.dlq").build();
    }
}
