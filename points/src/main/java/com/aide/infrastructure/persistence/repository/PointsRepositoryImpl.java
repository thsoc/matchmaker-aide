package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.PointsDo;
import com.aide.domain.repository.PointsRepository;
import com.aide.infrastructure.persistence.entity.Points;
import com.aide.infrastructure.persistence.mapper.PointsMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 积分仓储实现类
 * @date 2026/6/14
 * @date 21:16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsRepositoryImpl implements PointsRepository {
    private final PointsMapper pointsMapper;
    @Override
    public boolean findByOrderNo(PointsDo pointsDo) {
        log.info("积分仓储开始查询积分，订单编号：{}", pointsDo.getOrderNo());
        Points one = new LambdaQueryChainWrapper<>(pointsMapper)
                .eq(Points::getOrderNo, pointsDo.getOrderNo())
                .eq(Points::getUserId, pointsDo.getUserId())
                .select(Points::getId)
                .one();
        if (one != null) {
            log.info("积分仓储查询到积分，订单编号：{}", pointsDo.getOrderNo());
            return true;
        }
        return false;
    }

    @Override
    public void save(PointsDo pointsDo) {
        //保存积分
        pointsMapper.insert(convertToEntity(pointsDo));
    }

    private Points convertToEntity(PointsDo pointsDo) {
        return Points.builder()
                .userId(pointsDo.getUserId())
                .orderNo(pointsDo.getOrderNo())
                .pointsType(pointsDo.getPointsType())
                .points(pointsDo.getPoints())
                .remark(pointsDo.getRemark())
                .pointsType(pointsDo.getPointsType())
                .createTime(pointsDo.getCreateTime())
                .updateTime(pointsDo.getUpdateTime())
                .createBy(pointsDo.getCreateBy())
                .updateBy(pointsDo.getUpdateBy())
                .build();
    }
}
