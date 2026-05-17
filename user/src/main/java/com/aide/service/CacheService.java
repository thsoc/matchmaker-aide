package com.aide.service;

public interface CacheService {

    /**
     * 保存用户信息到缓存（同时保存到本地缓存和Redis）
     * @param key 缓存键
     * @param value 缓存值（可以是对象，会自动序列化）
     * @param expireSeconds 过期时间（秒）
     */
    void setUserCache(String key, Object value, long expireSeconds);


    /**
     * 从缓存获取用户信息（优先从本地缓存获取，未命中则从Redis获取）
     * @param key 缓存键
     * @return 缓存值（自动反序列化为对象）
     */
    <T> T getUserCache(String key, Class<T> clazz);

    /**
     * 删除用户缓存（同时删除本地缓存和Redis缓存）
     * @param key 缓存键
     */
    void deleteUserCache(String key);
}
