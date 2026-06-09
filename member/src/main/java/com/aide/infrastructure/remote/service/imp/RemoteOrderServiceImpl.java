package com.aide.infrastructure.remote.service.imp;

import com.aide.infrastructure.remote.feign.OrderFeignClient;
import com.aide.infrastructure.remote.service.RemoteOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 远程订单服务实现类
 * @date 2026/6/9
 * @date 18:02
 */
@Service
@RequiredArgsConstructor
public class RemoteOrderServiceImpl implements RemoteOrderService {
    private final OrderFeignClient orderFeignClient;

    @Override
    public Long createOrder(Long userId, Integer orderType, BigDecimal amount, String description) {
        return orderFeignClient.createOrder(userId, orderType, amount, description);
    }
}
