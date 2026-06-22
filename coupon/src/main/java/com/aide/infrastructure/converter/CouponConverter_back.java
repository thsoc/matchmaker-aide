//package com.aide.infrastructure.converter;
//
//import com.aide.domain.model.CouponDo;
//import com.aide.infrastructure.persistence.entity.Coupon;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//
///**
// * @author mazg
// * @description TODO
// * @date 2026/6/23
// * @date 03:50
// */
//@Mapper(componentModel = "spring")
//public interface CouponConverter {
//
//    /**
//     * MapStruct 会自动识别 CouponDo.rebuildBuilder()
//     * 并且自动把 CouponPO 中的字段映射到 Builder 的同名方法上！
//     */
//    @Mapping(target = "period", expression = "java(new CouponPeriod(po.getEffectiveTime(), po.getExpireTime()))")
//    @Mapping(target = "quota", expression = "java(new CouponQuota(po.getTotalCount(), po.getUsedCount()))")
//    CouponDo toDomain(Coupon po);
//}
