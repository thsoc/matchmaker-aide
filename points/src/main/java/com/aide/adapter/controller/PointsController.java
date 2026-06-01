package com.aide.adapter.controller;

import com.aide.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mazg
 * @description 积分控制器
 * @date 2026/5/29
 * @date 11:30
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class PointsController {
    private final PointsService pointsService;


}
