package com.aide.common.context;


import com.aide.common.constant.Constant;
import org.slf4j.MDC;

/**
 * @author mazg
 * @description 添加全链路跟踪
 * @date 2026/6/30
 * @date 12:34
 */
public class TraceContext {

    /**
     * 获取当前线程的 traceId
     */
    public static String getTraceId() {
        return MDC.get(Constant.TRACE_ID_KEY);  // 直接从 MDC 取
    }
}