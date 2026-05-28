package com.aide.infrastructure.persistence.mapper;

import com.aide.infrastructure.persistence.entity.RechargeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mazg
 * @description 充值记录Mapper
 * @date 2026/5/25
 */
@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {
}
