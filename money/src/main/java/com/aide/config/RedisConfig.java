package com.aide.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author mazg
 * @date 2026/5/19 18:53
 * @deprecated Redis配置类
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String REDIS_HOST;

    @Value("${spring.redis.port}")
    private String REDIS_PORT;

    @Value("${spring.redis.password}")
    private String REDIS_PASSWORD;

//    @Value("${spring.redis.password}")
//    private String REDIS_password;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // --- 配置 ObjectMapper ---
        // 为 GenericJackson2JsonRedisSerializer 创建一个专门的 ObjectMapper 实例
        ObjectMapper mapper = new ObjectMapper();
        // 复制原有配置，确保功能一致
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认类型信息，这对于反序列化回原始对象类型至关重要
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 注册 JavaTimeModule 以支持 Java 8 日期时间类型（LocalDateTime等）
        mapper.registerModule(new JavaTimeModule());

        // --- 配置序列化器 ---
        // 2. 使用 GenericJackson2JsonRedisSerializer 替换 Jackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用 GenericJackson2Json
        template.setValueSerializer(serializer);
        // hash的value序列化方式采用 GenericJackson2Json
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建ObjectMapper实例并注册JavaTimeModule以支持Java 8日期时间类型
     *
     * @return ObjectMapper实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册JavaTimeModule以支持Java 8日期时间类型
        mapper.registerModule(new JavaTimeModule());
        // 可以在这里统一配置ObjectMapper的各种属性
        return mapper;
    }

    // ================== Redisson 配置 ==================
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT + "")
                .setDatabase(0)
//                .setUsername("admin")
                .setPassword(REDIS_PASSWORD)
                .setConnectionPoolSize(20)
                .setConnectionMinimumIdleSize(5)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(10000)
                .setTimeout(3000);

        // 使用 JSON 序列化
        config.setCodec(new JsonJacksonCodec());

        return Redisson.create(config);
    }
}
