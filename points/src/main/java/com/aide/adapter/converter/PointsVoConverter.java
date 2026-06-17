package com.aide.adapter.converter;

import com.aide.common.dto.points.AddPointsRequest;
import com.aide.domain.model.PointsDo;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/14
 * @date 20:46
 */
@Component
public class PointsVoConverter {
    public PointsDo fromAddPointsRequest(AddPointsRequest addPointsRequest) {
        if (addPointsRequest == null){
            return null;
        }
        return PointsDo.builder()
                .userId(addPointsRequest.getUserId())
                .points(addPointsRequest.getPoints())
                .build();
    }
}
