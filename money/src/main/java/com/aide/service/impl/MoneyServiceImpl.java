package com.aide.service.impl;

import com.aide.entity.PO.Money;
import com.aide.mapper.MoneyMapper;
import com.aide.service.MoneyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 20721
 * @description 金额服务实现类
 * @date 2026/5/25
 * @date 15:53
 */
@Slf4j
@Service
public class MoneyServiceImpl extends ServiceImpl<MoneyMapper, Money> implements MoneyService {
    @Override
    public Long getMoney(String account) {
        return 0L;
    }
}
