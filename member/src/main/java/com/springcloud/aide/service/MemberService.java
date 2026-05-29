package com.springcloud.aide.service;

public interface MemberService {

    /**
     * 购买会员
     * @param userId 用户ID
     * @param memberType 会员类型：1-普通会员 2-高级会员 3-VIP会员
     * @return 购买结果消息
     */
    String buyMember(Long userId, Integer memberType);
}
