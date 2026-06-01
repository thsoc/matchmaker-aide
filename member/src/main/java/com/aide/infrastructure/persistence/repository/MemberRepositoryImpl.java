package com.aide.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aide.domain.model.MemberDo;
import com.aide.domain.repository.MemberRepository;
import com.aide.infrastructure.persistence.entity.Member;
import com.aide.infrastructure.persistence.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 会员仓储实现
 * @date 2026/5/29
 * @date 12:44
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberMapper memberMapper;

    @Override
    public void save(MemberDo memberDo) {
        Member member = convertToEntity(memberDo);
        if (member.getId() == null) {
            member.setCreateTime(LocalDateTime.now());
            memberMapper.insert(member);
        } else {
            member.setUpdateTime(LocalDateTime.now());
            memberMapper.updateById(member);
        }
        log.info("保存会员信息，用户ID: {}, 会员ID: {}", memberDo.getUserId(), member.getId());
    }

    @Override
    public MemberDo findByUserId(Long userId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getUserId, userId);
        wrapper.orderByDesc(Member::getCreateTime);
        wrapper.last("LIMIT 1");

        Member member = memberMapper.selectOne(wrapper);
        return member != null ? convertToDomain(member) : null;
    }

    @Override
    public MemberDo findById(Long id) {
        Member member = memberMapper.selectById(id);
        return member != null ? convertToDomain(member) : null;
    }

    private MemberDo convertToDomain(Member member) {
        return MemberDo.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .memberType(member.getMemberType())
                .status(member.getStatus())
                .startTime(member.getStartTime())
                .endTime(member.getEndTime())
                .price(member.getPrice())
                .createTime(member.getCreateTime())
                .updateTime(member.getUpdateTime())
                .build();
    }

    private Member convertToEntity(MemberDo memberDo) {
        return Member.builder()
                .id(memberDo.getId())
                .userId(memberDo.getUserId())
                .memberType(memberDo.getMemberType())
                .status(memberDo.getStatus())
                .startTime(memberDo.getStartTime())
                .endTime(memberDo.getEndTime())
                .price(memberDo.getPrice())
                .createTime(memberDo.getCreateTime())
                .updateTime(memberDo.getUpdateTime())
                .build();
    }
}
