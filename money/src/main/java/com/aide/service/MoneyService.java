package com.aide.service;

import com.aide.entity.PO.Money;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MoneyService extends IService<Money> {
    Long getMoney(String account);
}
