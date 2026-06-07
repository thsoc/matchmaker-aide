//package com.aide.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.env.*;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author mazg
// * @description TODO
// * @date 2026/6/7
// * @date 21:57
// */
//@Component
//@Slf4j
//public class ShardingSphereConfigAnalysis implements ApplicationRunner {
//
//    @Autowired
//    private Environment environment;
//
//    @Override
//    public void run(ApplicationArguments args) {
//        log.info("=== 分析ShardingSphere配置处理 ===");
//
//        // 1. 查看Spring Environment中的所有相关配置
//        Map<String, Object> allProps = new HashMap<>();
//        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
//
//        for (PropertySource<?> propertySource : propertySources) {
//            if (propertySource instanceof EnumerablePropertySource) {
//                for (String propertyName : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
//                    if (propertyName.contains("shardingsphere.datasource.ds0")) {
//                        Object value = propertySource.getProperty(propertyName);
//                        allProps.put(propertyName, value);
//                        log.info("配置项: {} = {}", propertyName, value);
//                    }
//                }
//            }
//        }
//
//        // 2. 特别检查hikari相关配置
//        log.info("\n=== hikari相关配置 ===");
//        allProps.entrySet().stream()
//                .filter(entry -> entry.getKey().contains("hikari"))
//                .forEach(entry -> log.info("{} = {}", entry.getKey(), entry.getValue()));
//
//        // 3. 分析配置结构
//        analyzeConfigurationStructure(allProps);
//    }
//
//    private void analyzeConfigurationStructure(Map<String, Object> props) {
//        log.info("\n=== 配置结构分析 ===");
//
//        // 找到所有ds0的配置
//        Map<String, Object> ds0Config = new HashMap<>();
//        props.forEach((key, value) -> {
//            if (key.startsWith("spring.shardingsphere.datasource.ds0.")) {
//                String subKey = key.substring("spring.shardingsphere.datasource.ds0.".length());
//                ds0Config.put(subKey, value);
//            }
//        });
//
//        // 按点号分割，分析层级结构
//        Map<String, List<String>> structure = new HashMap<>();
//        ds0Config.forEach((key, value) -> {
//            if (key.contains(".")) {
//                String firstLevel = key.substring(0, key.indexOf('.'));
//                structure.computeIfAbsent(firstLevel, k -> new ArrayList<>())
//                        .add(key.substring(key.indexOf('.') + 1));
//            }
//        });
//
//        structure.forEach((level, subKeys) -> {
//            log.info("一级配置 '{}' 有子项: {}", level, subKeys);
//        });
//    }
//}