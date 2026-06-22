package com.aide.common.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 获取系统时钟
 * @date 2026/6/23
 * @date 01:54
 */
@Component
public class SystemClock implements IClock{
    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now(); // 真实环境获取当前时间
    }
}
