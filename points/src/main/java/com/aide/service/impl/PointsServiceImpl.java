package com.aide.service.impl;

import com.aide.common.dto.points.AddPointsRequest;
import com.aide.adapter.converter.PointsVoConverter;
import com.aide.domain.model.PointsDo;
import com.aide.domain.service.PointsDomainService;
import com.aide.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mazg
 * @description 会员服务实现类
 * @date 2026/5/29
 * @date 11:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {
    private final PointsDomainService pointsDomainService;
    private final PointsVoConverter pointsVoConverter;

    @Transactional
    @Override
    public void addPoints(AddPointsRequest addPointsRequest) {
        log.info("会员服务开始发放积分,用户id:{}，发放积分是：{}", addPointsRequest.getUserId(), addPointsRequest.getPoints());

        //转换成领域对象
        PointsDo pointsDo = pointsVoConverter.fromAddPointsRequest(addPointsRequest);

        //发放积分
        pointsDomainService.addPoints(pointsDo);

    }
}
