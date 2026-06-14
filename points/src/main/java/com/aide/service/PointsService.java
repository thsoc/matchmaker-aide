package com.aide.service;

import com.aide.adapter.VO.AddPointsRequest;

import javax.validation.Valid;

public interface PointsService {
    void addPoints(AddPointsRequest addPointsRequest);
}
