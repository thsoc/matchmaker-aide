package com.aide.adapter.job;

import com.aide.service.CouponService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 定时任务
 * @date 2026/6/24
 * @date 20:26
 */
@Slf4j
@Component
public class CouponHandler {
    @Autowired
    private CouponService couponService;
    /**
     * 定时任务，预热快生效的优惠券，保存到redis,
     * 可使用事件发布(运营在后台点「发布秒杀活动」→ 调用预热接口 → 写 Redis + 初始化库存)
     */
    @XxlJob("preheatCouponHandler")
    public void preheatCouponHandler() {
        //提前时间（分钟）
        String param = XxlJobHelper.getJobParam();
        log.info("XXL-JOB: 预热快生效的优惠券开始执行，参数为：{}", param);
        // 具体的业务逻辑
        couponService.preheatCoupon(param);
        System.out.println("XXL-JOB: 任务执行成功！");
    }
}
