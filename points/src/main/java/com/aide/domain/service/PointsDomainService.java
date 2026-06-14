package com.aide.domain.service;

import com.aide.domain.model.PointsDo;
import com.aide.domain.repository.PointsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 积分领域服务
 * @date 2026/6/14
 * @date 20:43
 */
@Service
@RequiredArgsConstructor
public class PointsDomainService {
    private final PointsRepository pointsRepository;

    public void vaildateAddPoints(PointsDo pointsDo) {
        if (pointsDo == null){
            return;
        }
        if (pointsDo.getUserId() == null){
            throw new RuntimeException("用户ID不能为空");
        }
        if (pointsDo.getPoints() == null){
            throw new RuntimeException("积分数不能为空");
        }
        if (pointsDo.getPoints() < 0){
            throw new RuntimeException("积分数不能小于0");
        }
        if (pointsDo.getPoints() > 100000){
            throw new RuntimeException("积分数不能大于100000");
        }
    }

    public void addPoints(PointsDo pointsDo) {
        //校验参数
        vaildateAddPoints(pointsDo);

        //查询积分是否存在
        checkExist(pointsDo);

        //初始化领域对象
        pointsDo.initFromAddPoints();

        //保存积分
        pointsRepository.save(pointsDo);


    }

    private void checkExist(PointsDo pointsDo) {
        boolean exist =pointsRepository.findByOrderNo(pointsDo);
        if (exist){
            throw new RuntimeException("积分已存在");
        }
    }
}
