package com.aide.domain.service;

import com.aide.common.Result.Result;
import com.aide.infrastructure.remote.service.RemoteMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 金额领域服务
 * @date 2026/6/9
 * @date 17:51
 */
@Service
@RequiredArgsConstructor
public class MoneyDomainService {
    private final RemoteMoneyService remoteMoneyService;

    /**
     * @author mazg
     * @description 扣款
     * @date 17:52 2026/6/9
     * @return
     **/
    public Result dudeceMoney(Long userId, BigDecimal amount, String description){
        return remoteMoneyService.deduct(userId, amount, description);
    }
}
