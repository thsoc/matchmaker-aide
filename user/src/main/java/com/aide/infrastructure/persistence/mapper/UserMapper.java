package com.aide.infrastructure.persistence.mapper;

import com.aide.domain.model.UserDo;
import com.aide.infrastructure.persistence.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author mazg
 * @description 用户MAP
 * @date 11:11 2026/5/20
 * @return
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {


    /**
     * 根据账号查询用户
     */
    User selectByAccount(@Param("account") String account);

    /**
     * 根据手机号查询用户
     */
    User selectByMobile(@Param("mobile") String mobile);

    /**
     * 根据用户ID修改数据
     */
    void updateUserById(@Param("user") User user);

    /**
     * 根据用户ID更新头像
     **/
    void updateAvatarById(@Param("avatar") String avatar, @Param("id") Long id);


    void updateMobileById(@Param("user") UserDo userDo);
}


