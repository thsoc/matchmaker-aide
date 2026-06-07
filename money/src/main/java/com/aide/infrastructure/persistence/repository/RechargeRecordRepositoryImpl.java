package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.repository.RechargeRecordRepository;
import com.aide.infrastructure.persistence.entity.RechargeRecord;
import com.aide.infrastructure.persistence.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 充值记录仓储实现 - 负责领域对象与持久化实体的转换
 *
 * 职责：
 * 1. 实现领域层定义的 Repository 接口
 * 2. 处理 RechargeRecordDo ↔ RechargeRecord 的转换
 * 3. 调用 MyBatis-Plus Mapper 进行数据库操作
 * @date 2026/5/28
 * @date 11:29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RechargeRecordRepositoryImpl implements RechargeRecordRepository {

    private final RechargeRecordMapper rechargeRecordMapper;

    @Override
    public RechargeRecordDo save(RechargeRecordDo rechargeRecord) {
        RechargeRecord entity = convertToEntity(rechargeRecord);

        if (rechargeRecord.getId() == null) {
            rechargeRecordMapper.insert(entity);
            log.debug("新增充值记录，用户ID: {}, 订单号: {}",
                    rechargeRecord.getUserId(), rechargeRecord.getOrderNo());
        } else {
            rechargeRecordMapper.updateById(entity);
            log.debug("更新充值记录，用户ID: {}, 状态: {}",
                    rechargeRecord.getUserId(), rechargeRecord.getStatus());
        }
        return convertToDomainObject(entity);
    }

    @Override
    public RechargeRecordDo findByOrderNo(String orderNo) {
        LambdaQueryWrapper<RechargeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeRecord::getOrderNo, orderNo);
        RechargeRecord record = rechargeRecordMapper.selectOne(queryWrapper);

        if (record == null) {
            return null;
        }

        return convertToDomainObject(record);
    }

    /**
     * 持久化实体 → 领域对象
     */
    private RechargeRecordDo convertToDomainObject(RechargeRecord entity) {
        return RechargeRecordDo.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .orderNo(entity.getOrderNo())
                .payType(entity.getPayType())
                .rechargeTime(entity.getRechargeTime())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .remark(entity.getRemark())
                .build();
    }

    /**
     * 领域对象 → 持久化实体
     */
    private RechargeRecord convertToEntity(RechargeRecordDo domainObject) {
        return RechargeRecord.builder()
                .id(domainObject.getId())
                .userId(domainObject.getUserId())
                .amount(domainObject.getAmount())
                .status(domainObject.getStatus())
                .orderNo(domainObject.getOrderNo())
                .payType(domainObject.getPayType())
                .rechargeTime(domainObject.getRechargeTime())
                .createTime(domainObject.getCreateTime())
                .updateTime(domainObject.getUpdateTime())
                .remark(domainObject.getRemark())
                .build();
    }

    @Override
    public RechargeRecordDo findById(Long id) {
        RechargeRecord record = rechargeRecordMapper.selectById(id);

        if (record == null) {
            return null;
        }

        return convertToDomainObject(record);
    }

    @Override
    public int updateStatusToSuccess(String orderNo, String transactionId) {
        LambdaUpdateWrapper<RechargeRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RechargeRecord::getOrderNo, orderNo)
                .ne(RechargeRecord::getStatus, 2) // status != 2（非成功状态）
                .set(RechargeRecord::getStatus, 2) // 设置为成功
                .set(RechargeRecord::getPaymentResult, transactionId)
                .set(RechargeRecord::getUpdateTime, LocalDateTime.now());

        int updatedRows = rechargeRecordMapper.update(null, updateWrapper);

        log.info("原子性更新充值记录状态为成功，订单号: {}, 交易号: {}, 影响行数: {}",
                orderNo, transactionId, updatedRows);

        return updatedRows;
    }

    @Override
    public int updateStatusToFailure(String orderNo, String failureReason) {
        LambdaUpdateWrapper<RechargeRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RechargeRecord::getOrderNo, orderNo)
                .in(RechargeRecord::getStatus, 0, 1) // 只允许待支付或充值中状态
                .set(RechargeRecord::getStatus, 3) // 设置为失败
                .set(RechargeRecord::getRemark, failureReason)
                .set(RechargeRecord::getUpdateTime, LocalDateTime.now());

        int updatedRows = rechargeRecordMapper.update(null, updateWrapper);

        log.info("原子性更新充值记录状态为失败，订单号: {}, 原因: {}, 影响行数: {}",
                orderNo, failureReason, updatedRows);

        return updatedRows;
    }
}
