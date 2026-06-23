-- KEYS[1] : 库存 key (例如 seckill:stock:1001)
-- KEYS[2] : 用户已购集合 key (例如 seckill:user_set:1001)
-- ARGV[1]: 用户 ID
-- ARGV[2]: 扣减数量（通常为1）
-- ARGV[3]: 总库存上限（可选，用于初始化检查）

-- 1. 检查用户是否已经购买过（一人一单）
local isMember = redis.call('SISMEMBER', KEYS[2], ARGV[1])
if isMember == 1 then
    return -2   -- 返回 -2 表示重复购买
end

-- 2. 获取当前库存
local stock = tonumber(redis.call('GET', KEYS[1]))
if not stock then
    -- 如果库存 key 不存在，视为售罄或未初始化
    return -3   -- 返回 -3 表示库存不存在
end

-- 3. 判断库存是否充足
if stock < tonumber(ARGV[2]) then
    return -1   -- 返回 -1 表示库存不足
end

-- 4. 扣减库存（原子操作）
redis.call('DECRBY', KEYS[1], ARGV[2])

-- 5. 记录用户已购买（加入集合）
redis.call('SADD', KEYS[2], ARGV[1])

-- 6. 设置集合过期时间（防止无限增长，可选）
--    假设活动结束时间已知，可传入 ARGV[4] 作为 TTL
-- if ARGV[4] then
--     redis.call('EXPIRE', KEYS[2], ARGV[4])
-- end

return 1   -- 成功