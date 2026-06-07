package com.aide.config;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/7
 * @date 22:18
 */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
        SpringContextUtil.environment = applicationContext.getEnvironment();
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}