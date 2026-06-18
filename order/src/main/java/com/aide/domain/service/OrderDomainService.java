package com.aide.domain.service;

import com.aide.adapter.converter.OrderVoConverter;
import com.aide.domain.model.OrderDo;
import com.aide.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 订单领域类
 * @date 2026/6/9
 * @date 18:05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDomainService {
    private final OrderRepository orderRepository;
    private final OrderVoConverter orderVoConverter;

    /**
     * @author mazg
     * @description 创建订单
     * @date 18:08 2026/6/9
     * @return 
     **/
    public String createOrder(Long userId, Integer orderType, BigDecimal amount, String description) {

        log.info("创建订单，用户ID: {}, 订单类型: {}, 金额: {}, 描述: {}", userId, orderType, amount, description);
        //将订单转换为领域对象
        OrderDo orderDo = orderVoConverter.fromOrderRequest(userId, orderType, amount, description);
        //随机生成订单编号
        orderDo.createOrderNo();
        //校验订单
        orderDo.validateFromBuyMenber();
        //初始化订单信息
        orderDo.initFromBuyMenber();
        orderRepository.createOrder(orderDo);
        return orderDo.getOrderNo();
    }

    public void changeOrderStatus(Long userId, String orderNo) {
        //查询订单是否存在
        OrderDo orderDo = orderRepository.getOrderByOrderNo(orderNo);
        //判断订单状态
        checkOrderStatus(orderDo);
        orderDo.setStatus("2");
        //修改订单状态
        int update = orderRepository.changeOrderStatus(userId, orderNo);
        if (update <= 0){
            log.error("修改订单状态失败，用户ID: {}, 订单编号: {}", userId, orderNo);
            throw new RuntimeException("修改订单状态失败");
        }
    }

    private void checkOrderStatus(OrderDo orderDo) {
        if (orderDo == null) {
            log.error("订单不存在，订单编号: {}", orderDo.getOrderNo());
            throw new RuntimeException("订单不存在");
        }
        if (!orderDo.getStatus().equals("1")) {
            log.error("订单状态错误，订单编号: {}, 订单状态: {}", orderDo.getOrderNo(), orderDo.getStatus());
            throw new RuntimeException("订单状态错误");
        }
    }
}
