package com.aide.infrastructure.remote.service;

import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 远程金额服务接口
 * @date 2026/6/9
 * @date 17:43
 */
public interface RemoteMoneyService {
    void deduct(Long userId,BigDecimal amount,String description);
}
