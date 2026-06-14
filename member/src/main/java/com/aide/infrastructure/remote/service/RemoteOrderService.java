package com.aide.infrastructure.remote.service;

import java.math.BigDecimal;

public interface RemoteOrderService {
    String createOrder(Long userId, Integer orderType, BigDecimal amount, String description);
}
