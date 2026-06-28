package com.aide.adapter.listener;

import com.aide.common.dto.feign.points.AddPointsRequest;
import com.aide.service.PointsService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author mazg
 * @description 消费者
 * @date 2026/6/28
 * @date 11:40
 */
@Component
public class OrderConsumer {
    @Autowired
    private PointsService pointsService;

    @RabbitListener(queues = "point.queue")
    public void process(AddPointsRequest dto, Channel channel, Message message) throws IOException {
        try {
            handle(dto);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // requeue=false → 进入 DLQ（需要队列配了 x-dead-letter-exchange）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    false, false);
        }
    }

    private void handle(AddPointsRequest dto) {
        pointsService.addPoints(dto);
    }
}