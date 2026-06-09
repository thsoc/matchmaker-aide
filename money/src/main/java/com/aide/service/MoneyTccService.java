package com.aide.service;

import com.aide.common.exception.MoneyException;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.math.BigDecimal;


@LocalTCC
public interface MoneyTccService {
    @TwoPhaseBusinessAction(
            name = "deductBalance",
            commitMethod = "confirmDeduct",
            rollbackMethod = "cancelDeduct",
            useTCCFence = true // 【核心】开启 TCC Fence 机制
    )
    boolean deductBalance(BusinessActionContext context,
                          @BusinessActionContextParameter(paramName = "userId") Long userId,
                          @BusinessActionContextParameter(paramName = "amount") BigDecimal amount) throws MoneyException;

    boolean confirmDeduct(BusinessActionContext context);

    boolean cancelDeduct(BusinessActionContext context);
}
