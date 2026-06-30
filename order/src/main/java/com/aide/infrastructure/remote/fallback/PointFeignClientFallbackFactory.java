package com.aide.infrastructure.remote.fallback;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.money.DeductRequest;
import com.aide.common.dto.feign.points.AddPointsRequest;
import com.aide.infrastructure.remote.feign.MoneyFeignClient;
import com.aide.infrastructure.remote.feign.PointsFeignClient;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/30
 * @date 16:24
 */
public class PointFeignClientFallbackFactory extends AbstractFallbackFactory<PointsFeignClient>{
    @Override
    public PointsFeignClient create(Throwable cause) {
        return new PointsFeignClient() {
            @Override
            public void addPoints(AddPointsRequest build) {
                defaultFail("points-service", "addPoints", cause);
            }
        };
    }
}
