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
public class OrderRabbitConfig {

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable("order.queue").build();
    }

    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder.directExchange("order.exchange").durable(true).build();
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with("order.routingKey");
    }

    // 死信队列
    @Bean
    public Queue orderDlq() {
        return QueueBuilder.durable("order.queue.dlq").build();
    }
}
