package com.aide.domain.repository;

import com.aide.domain.model.PointsDo;

public interface PointsRepository {
    boolean findByOrderNo(PointsDo pointsDo);

    void save(PointsDo pointsDo);
}
