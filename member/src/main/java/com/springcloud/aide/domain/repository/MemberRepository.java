package com.springcloud.aide.domain.repository;

import com.springcloud.aide.domain.model.MemberDo;

/**
 * 会员仓储接口
 */
public interface MemberRepository {

    /**
     * 保存会员信息
     */
    void save(MemberDo memberDo);

    /**
     * 根据用户ID查询会员
     */
    MemberDo findByUserId(Long userId);

    /**
     * 根据ID查询会员
     */
    MemberDo findById(Long id);
}
