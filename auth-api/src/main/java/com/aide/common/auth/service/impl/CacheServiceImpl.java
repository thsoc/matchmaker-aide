package com.aide.common.auth.service.impl;

import com.aide.common.auth.service.CacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CacheServiceImpl implements CacheService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    private static final String USER_CACHE_PREFIX = "user:cache:";
    private static final long DEFAULT_EXPIRE_SECONDS = 7200; // 默认2小时

    // 本地缓存（Caffeine），设置最大1000条记录，写入后2小时过期
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS)
            .recordStats()
            .build();

    @Override
    public <T> T getUserCache(String key, Class<T> clazz) {
        try {
            String cacheKey = USER_CACHE_PREFIX + key;

            // 1. 先从本地缓存获取
            Object localValue = localCache.getIfPresent(cacheKey);
            if (localValue != null  && clazz.isInstance(localValue)) {
                log.debug("从本地缓存获取用户信息成功，key: {}", cacheKey);
                return  (T) localValue;
            }

            // 2. 本地缓存未命中，从Redis获取（RedisTemplate会自动反序列化）
            Object redisValue = redisTemplate.opsForValue().get(cacheKey);
            if (redisValue != null && clazz.isInstance(redisValue)) {
                // 将Redis中的数据回写到本地缓存
                localCache.put(cacheKey, redisValue);
                log.debug("从Redis缓存获取用户信息并回写本地缓存，key: {}", cacheKey);
                return (T) redisValue;
            }

            log.debug("缓存未命中，key: {}", cacheKey);
            return null;

        } catch (Exception e) {
            log.error("获取用户缓存失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    public void setUserCache(String key, Object value, long expireSeconds) {
        try {
            String cacheKey = USER_CACHE_PREFIX + key;

            //服务不做权限认证了
//            // 1. 先保存到本地缓存
//            localCache.put(cacheKey, value);
//            log.debug("用户信息已保存到本地缓存，key: {}", cacheKey);

            //这边是为了实现“强制下线”、“Token 黑名单”或“动态修改权限”
            // 2. 再保存到Redis（RedisTemplate会自动序列化）
            redisTemplate.opsForValue().set(cacheKey, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("用户信息已保存到Redis缓存，key: {}", cacheKey);

        } catch (Exception e) {
            log.error("设置用户缓存失败，key: {}", key, e);
        }
    }

    @Override
    public void deleteUserCache(String key) {
        try {
            String cacheKey = USER_CACHE_PREFIX + key;

            // 1. 删除本地缓存
            localCache.invalidate(cacheKey);
            log.debug("本地缓存已删除，key: {}", cacheKey);

            // 2. 删除Redis缓存
            redisTemplate.delete(cacheKey);
            log.debug("Redis缓存已删除，key: {}", cacheKey);

        } catch (Exception e) {
            log.error("删除用户缓存失败，key: {}", key, e);
        }
    }

    /**
     * 获取缓存统计信息（用于监控）
     */
    public String getCacheStats() {
        return localCache.stats().toString();
    }
}
