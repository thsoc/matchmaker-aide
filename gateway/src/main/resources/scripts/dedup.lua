-- KEYS[1]: 去重Key, ARGV[1]: 过期时间(毫秒)
if redis.call("EXISTS", KEYS[1]) == 1 then
    return 0 -- 重复请求
else
    redis.call("SET", KEYS[1], "1", "PX", tonumber(ARGV[1]))
    return 1 -- 首次请求
end