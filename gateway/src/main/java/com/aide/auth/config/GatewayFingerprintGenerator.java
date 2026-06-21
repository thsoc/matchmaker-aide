package com.aide.auth.config;


import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mazg
 * @description 指纹生成器
 * @date 2026/6/21
 * @date 16:03
 */
public class GatewayFingerprintGenerator {

    // 忽略的参数
    private static final Set<String> IGNORE_PARAMS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "timestamp", "_t", "traceId", "nonce"
    )));

    /**
     * 生成统一防重指纹
     */
    public static String generate(String userId, String path, MultiValueMap<String, String> queryParams, String bodyStr) {
        String queryHash = generateQueryHash(queryParams);
        String bodyHash = generateBodyHash(bodyStr);

        // 格式: dedup:{userId}:{path}:{queryHash}:{bodyHash}
        return String.format("dedup:%s:%s:%s:%s", userId, path, queryHash, bodyHash);
    }

    private static String generateQueryHash(MultiValueMap<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) return "no-query";

        // 1. 过滤掉不需要参与计算的参数（如时间戳）
        // 2. 对 Key 进行排序，保证参数顺序不影响结果
        // 3. 对 Value 列表也进行排序（防止 ?id=1&id=2 和 ?id=2&id=1 产生不同哈希）
        //先key排序，再对同一个key的value排序
        String normalized = queryParams.entrySet().stream()
                .filter(e -> !IGNORE_PARAMS.contains(e.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .flatMap(e -> e.getValue().stream().sorted().map(v -> e.getKey() + "=" + v))
                .collect(Collectors.joining("&"));

        return sha256(normalized);
    }

    private static String generateBodyHash(String bodyStr) {
        if (bodyStr == null || bodyStr.trim().isEmpty())
            return "no-body";
        return sha256(bodyStr);
    }

    /**
     *
     * @param input
     * @return
     */
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}