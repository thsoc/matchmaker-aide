package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.OrderDo;
import com.aide.domain.repository.OrderRepository;
import com.aide.infrastructure.persistence.entity.Order;
import com.aide.infrastructure.persistence.mapper.OrderMapper;
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

    private Order fromOrderDo(OrderDo orderDo) {
        return Order.builder()
                .id(orderDo.getId())
                .userId(orderDo.getUserId())
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
