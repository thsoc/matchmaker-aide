package com.aide.service;

import java.math.BigDecimal;

public interface MemberService {

    /**
     * 购买会员
     * @param userId 用户ID
     * @param memberType 会员类型：1-普通会员 2-高级会员 3-VIP会员
     * @return 购买结果消息
     */
    String buyMember(Long userId, Integer memberType);

    /**
     * 获取会员不同类型价格信息
     * @param memberType 会员类型：1-普通会员 2-高级会员 3-VIP会员
     * @return 价格信息
     */
    BigDecimal getMemberPrice(Integer memberType);
}
