package com.aide.adapter.converter;

import com.aide.adapter.dto.RechargeResponseDTO;
import com.aide.domain.model.RechargeRecordDo;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 充值记录 DTO 转换器
 *
 * 职责：
 * 1. 领域对象 → DTO 转换
 * 2. DTO → 领域对象转换（如果需要）
 * @date 2026/5/28
 * @date 11:40
 */
@Component
public class RechargeRecordConverter {

    /**
     * 领域对象转响应 DTO
     */
    public RechargeResponseDTO toResponseDTO(RechargeRecordDo record) {
        if (record == null) {
            return null;
        }

        return RechargeResponseDTO.builder()
                .rechargeId(record.getId())
                .userId(record.getUserId())
                .amount(record.getAmount())
                .status(record.getStatus())
                .orderNo(record.getOrderNo())
                .rechargeTime(record.getRechargeTime())
                .build();
    }

    /**
     * 请求 DTO 转领域对象（如果需要）
     */
    public RechargeRecordDo fromRequestDTO(Long userId, String orderNo) {
        // 如果需要从 DTO 创建领域对象
        return RechargeRecordDo.builder()
                .userId(userId)
                .orderNo(orderNo)
                .build();
    }
}
