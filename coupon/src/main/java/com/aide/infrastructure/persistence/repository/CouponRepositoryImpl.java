package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.CouponDo;
import com.aide.domain.repository.CouponRepository;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.mapper.CouponMapper;
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
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponMapper orderMapper;

    @Override
    public String createOrder(CouponDo orderDo) {
        log.info("保存订单，订单ID: {}", orderDo.getId());
        //将领域对象转换成持久化对象
        Coupon order = fromOrderDo(orderDo);
        orderMapper.insert(order);
        return order.getId().toString();
    }

    private Coupon fromOrderDo(CouponDo orderDo) {
        return Coupon.builder()
                .id(orderDo.getId())
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
