package com.aide.service;

import com.aide.common.dto.feign.points.AddPointsRequest;

public interface PointsService {
    void addPoints(AddPointsRequest addPointsRequest);
}
