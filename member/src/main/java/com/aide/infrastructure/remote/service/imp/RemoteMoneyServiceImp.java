package com.aide.infrastructure.remote.service.imp;

import com.aide.infrastructure.remote.feign.MoneyFeignClient;
import com.aide.infrastructure.remote.service.RemoteMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 远程金额服务实现类
 * @date 2026/6/9
 * @date 17:46
 */
@Service
@RequiredArgsConstructor
public class RemoteMoneyServiceImp implements RemoteMoneyService {

    private final MoneyFeignClient moneyFeignClient;

    @Override
    public void deduct(Long userId, BigDecimal amount, String description) {
        moneyFeignClient.deduct(userId, amount,description);
    }
}
