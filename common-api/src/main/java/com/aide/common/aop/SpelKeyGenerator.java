package com.aide.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author mazg
 * @description spel表达式解析工具类
 * @date 2026/6/20
 * @date 21:16
 */
//@Component
public class SpelKeyGenerator {

    // 1. 单例化解析器，避免重复创建带来的性能开销
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    // 2. 参数名发现器，用于获取方法签名中的真实参数名
    private static final DefaultParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 解析 SpEL 表达式，生成最终的 Key
     * @param keyExpression 注解上的 SpEL 表达式，如 "#createOrderReq.productId"
     * @param joinPoint     AOP 切入点，包含方法参数等信息
     * @return 解析后的真实 Key
     */
    public static String parseKey(String keyExpression, JoinPoint joinPoint) {
        if (keyExpression == null || keyExpression.isEmpty()) {
            return null;
        }

        // 1. 获取方法签名和参数值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 2. 获取方法的真实参数名数组 (如 ["createOrderReq"])
        // 注意：Java 编译后默认不保留参数名，需要借助此工具从字节码局部变量表中读取
        String[] paramNames = DISCOVERER.getParameterNames(method);

        // 3. 创建 SpEL 评估上下文（相当于给表达式提供一个变量环境）
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 4. 核心步骤：将参数名和参数值一一绑定到上下文中
        // 这样 SpEL 解析器遇到 #createOrderReq 时，才知道它对应 args[0]
        if (paramNames != null && paramNames.length > 0) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 5. 解析表达式并计算结果
        Expression expression = PARSER.parseExpression(keyExpression);
        return expression.getValue(context, String.class);
    }
}