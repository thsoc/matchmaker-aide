package com.aide.common.exception;

import com.aide.common.Result.BizCodeEnum;
import com.aide.common.Result.Result;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * @author mazg
 * @description 拦截error
 * HttpServletRequest与webflux冲突，如果网关非要引用，使用condition注解
 * @date 2026/6/30
 * @date 12:22
 */
@Component
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Result<Void> handleError(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        // 根据状态码映射到统一的业务错误码
        BizCodeEnum bizCode = mapHttpStatusToBizCode(status);
        return Result.error(bizCode.getCode(), status.getReasonPhrase());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private BizCodeEnum mapHttpStatusToBizCode(HttpStatus status) {
        if (status.is4xxClientError()) { // 是不是4xx异常
            return BizCodeEnum.CLIENT_ERROR;
        }
        return BizCodeEnum.SYSTEM_ERROR;
    }
}