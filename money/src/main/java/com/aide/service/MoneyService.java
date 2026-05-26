package com.aide.service;

import com.aide.infrastructure.persistence.entity.Money;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MoneyService extends IService<Money> {
    Long getMoney(String account);
}
