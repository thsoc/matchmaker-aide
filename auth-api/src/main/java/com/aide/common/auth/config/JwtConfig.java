package com.aide.common.auth.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret:mySecretKeyForMatchmakerAideApplicationWhichIsLongEnough}")
    private String secret;

    @Value("${jwt.expiration:7200}")
    private Long expiration;

    @Bean
    public SecretKey jwtSecretKey() {
        // 确保密钥长度足够（至少256位）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // 如果密钥太短，使用Base64编码的固定密钥
            String defaultSecret = "mySecretKeyForMatchmakerAideApplicationWhichIsLongEnough";
            keyBytes = Base64.getEncoder().encode(defaultSecret.getBytes(StandardCharsets.UTF_8));
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public Long jwtExpiration() {
        return expiration;
    }
}
