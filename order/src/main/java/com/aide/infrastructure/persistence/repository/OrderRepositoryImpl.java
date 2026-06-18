package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.OrderDo;
import com.aide.domain.repository.OrderRepository;
import com.aide.infrastructure.persistence.entity.Order;
import com.aide.infrastructure.persistence.mapper.OrderMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 订单仓储实现类，基础仓储实现类
 * @date 2026/6/14
 * @date 16:35
 */
@Slf4j
@Component
@AllArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderMapper orderMapper;

    @Override
    public String createOrder(OrderDo orderDo) {
        log.info("保存订单，订单ID: {}", orderDo.getId());
        //将领域对象转换成持久化对象
        Order order = fromOrderDo(orderDo);
        orderMapper.insert(order);
        return order.getId().toString();
    }

    @Override
    public OrderDo getOrderByOrderNo(String orderNo) {
        log.info("查询订单，订单编号: {}", orderNo);
        Order one = new LambdaQueryChainWrapper<>(orderMapper)
                .eq(Order::getOrderNo, orderNo).one();
        if (one == null) {
            log.info("查询订单失败，订单编号: {}", orderNo);
            return null;
        }
        OrderDo orderDo = fromOrder(one);
        return orderDo;
    }

    @Override
    public int changeOrderStatus(Long userId, String orderNo) {
        LambdaUpdateChainWrapper<Order> set = new LambdaUpdateChainWrapper<>(orderMapper)
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId)
                .set(Order::getStatus, "2");
        int update = orderMapper.update(null, set);
        return update;
    }

    private OrderDo fromOrder(Order one) {
        return OrderDo.builder()
                .id(one.getId())
                .userId(one.getUserId())
                .orderNo(one.getOrderNo())
                .orderType(one.getOrderType())
                .amount(one.getAmount())
                .description(one.getDescription())
                .status(one.getStatus())
                .createTime(one.getCreateTime())
                .updateTime(one.getUpdateTime())
                .deleteTime(one.getDeleteTime())
                .remark(one.getRemark())
                .version(one.getVersion())
                .createBy(one.getCreateBy())
                .updateBy(one.getUpdateBy())
                .build();
    }

    private Order fromOrderDo(OrderDo orderDo) {
        return Order.builder()
                .id(orderDo.getId())
                .userId(orderDo.getUserId())
                .orderNo(orderDo.getOrderNo())
                .orderType(orderDo.getOrderType())
                .amount(orderDo.getAmount())
                .description(orderDo.getDescription())
                .status(orderDo.getStatus())
                .createTime(orderDo.getCreateTime())
                .updateTime(orderDo.getUpdateTime())
                .deleteTime(orderDo.getDeleteTime())
                .remark(orderDo.getRemark())
                .version(orderDo.getVersion())
                .createBy(orderDo.getCreateBy())
                .updateBy(orderDo.getUpdateBy())
                .build();
    }
}
