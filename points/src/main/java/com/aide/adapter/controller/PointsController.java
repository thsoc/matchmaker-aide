package com.aide.adapter.controller;

import com.aide.adapter.VO.AddPointsRequest;
import com.aide.common.Result.Result;
import com.aide.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author mazg
 * @description 积分控制器
 * @date 2026/5/29
 * @date 11:30
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {
    private final PointsService pointsService;

    /**
     * 发放积分
     * @param addPointsRequest
     * @return
     */
    @PostMapping("/addPoints")
    public Result<Void> addPoints(@Valid @RequestBody AddPointsRequest addPointsRequest) {
        pointsService.addPoints(addPointsRequest);
        return Result.success();
    }
}
