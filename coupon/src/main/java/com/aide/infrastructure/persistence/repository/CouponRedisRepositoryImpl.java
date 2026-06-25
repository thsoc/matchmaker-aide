package com.aide.infrastructure.persistence.repository;

import com.aide.domain.repository.CouponRedisRepository;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.mapper.CouponMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * @author mazg
 * @description 优惠仓储实现类，基础仓储实现类
 * @date 2026/6/14
 * @date 16:35
 */
@Slf4j
@Component
@AllArgsConstructor
public class CouponRedisRepositoryImpl implements CouponRedisRepository {
    private final DefaultRedisScript<Long> deducedCouponScript;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponMapper couponMapper;
    private final String STOCK_KEY_PREFIX = "seckill:stock:";
    private final String USERS_KEY_PREFIX = "seckill:user_set:";

    @Override
    public String deduceCoupon(Long id, Long userId) {

        // 准备 keys 和 args
        List<String> keys = Arrays.asList(STOCK_KEY_PREFIX + id, USERS_KEY_PREFIX + id);
        List<String> args = Arrays.asList(userId.toString(), "1");

        //执行lua脚本
        log.info(">>> 优惠券开始执行Lua脚本");
        Long execute = redisTemplate.execute(deducedCouponScript, keys, args);
        switch (execute.intValue()) {
            case 1:
                return "抢购成功！";
            case -1:
                System.out.println("库存不足");
                throw new RuntimeException("库存不足");
            case -2:
                System.out.println("每人限购一件");
                throw new RuntimeException("您已抢购过");
            case -3:
                System.out.println("活动尚未开始或已结束");
                throw new RuntimeException("活动尚未开始或已结束");
        }
        return null;
    }

    /**
     * @author mazg
     * @description 预热快生效的优惠券，保存到redis
     * @date 20:34 2026/6/24
     * @return
     **/
    @Override
    public void preheatCoupon(int advanceTime) {
        try {
            long now = System.currentTimeMillis();
            //查询快生效的优惠券（提前时间小于等于advanceTime分钟）
            LambdaQueryChainWrapper<Coupon> couponLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(couponMapper);
            couponLambdaQueryChainWrapper.eq(Coupon::getStatus, "0")
                    .le(Coupon::getEffectiveTime, now + advanceTime * 60 * 1000)
                    .ge(Coupon::getExpireTime, now);// todo 要平衡提前时间和执行频率
            List<Coupon> coupons = couponLambdaQueryChainWrapper.list();
            //将这些数据保持到redis中,key为优惠券id，value为优惠券数量
            for (Coupon coupon : coupons) {
                redisTemplate.opsForValue().setIfAbsent(STOCK_KEY_PREFIX + coupon.getId(), coupon.getAvailableStock());
            }
        } catch (Exception e){
            log.info("出现异常防止被xxl-job吞掉：{}", e);
            throw e;
        }

    }
}
