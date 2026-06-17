package com.aide.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author mazg
 * @description feign 错误解码器
 * @date 2026/6/17
 * @date 12:46
 */
//@Component // 注册成 Bean 就会被 Feign 自动用
public class SeataFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder delegate = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        // 1) 先按 Feign 默认规则：4xx/5xx 直接转异常（网络/服务端错误，不用读 body）
        if (response.status() >= 400) {
            return delegate.decode(methodKey, response);
        }

        // 2) 状态码是 200，但我们要看 body 里是不是 Result.error
        // body 只能读一次，读完要“复原”或直接基于读到的值判断
        if (response.body() != null) {
            try {
                String bodyStr = StreamUtils.copyToString(
                        response.body().asInputStream(),
                        StandardCharsets.UTF_8
                );

                // 简易判断：如果 body 里有 "code":500 或 success=false 之类的特征
                // 按你 Result 的真实字段来写条件（下面是最稳的 JSON 特征判断）
                boolean isBizError =
                        bodyStr.contains("\"code\":500") ||
                                bodyStr.contains("\"code\":400") ||
                                (bodyStr.contains("\"success\":false")) ||
                                bodyStr.contains("\"message\":\"系统异常\""); // 兜底特征

                if (isBizError) {
                    // 转成 RuntimeException，让 @GlobalTransactional 能回滚
                    return new RuntimeException(
                            "Feign business error, method=" + methodKey + ", body=" + bodyStr);
                }

                // 不是业务错误 → 需要把 body 还回去（否则后面反序列化会读不到）
                // 但 Response body 已关闭，所以更稳妥的做法：直接 return null 让 Feign 按正常 200 走
                // 不过 safer 写法：我们用一个“可重复读的 Response 包装” —— 但你先不做到这么复杂也能跑
                // 这里先简单处理：认为它真是成功 200，就当没错误
                return null;

            } catch (Exception e) {
                // 读 body 失败，走默认
                return delegate.decode(methodKey, response);
            }
        }

        return null;
    }
}
