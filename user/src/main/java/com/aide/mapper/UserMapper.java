package com.aide.mapper;

import com.aide.entity.PO.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

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
     * 根据邮箱查询用户
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 分页查询用户列表
     */
    List<User> selectUserPage(@Param("user") User user,
                              @Param("startTime") String startTime,
                              @Param("endTime") String endTime);

    /**
     * 批量插入用户
     */
    int insertBatch(@Param("list") List<User> users);

    /**
     * 根据ID逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除
     */
    int deleteBatchIds(@Param("ids") List<Long> ids);

    /**
     * 统计用户数量
     */
    Integer countUsers(@Param("status") String status,
                       @Param("role") String role,
                       @Param("startTime") String startTime,
                       @Param("endTime") String endTime);

    /**
     * 检查账号是否存在
     */
    Boolean existsByAccount(@Param("account") String account);

    /**
     * 检查手机号是否存在
     */
    Boolean existsByMobile(@Param("mobile") String mobile);

    /**
     * 更新最后登录信息
     */
    void updateLastLoginInfo(@Param("id") Long id,
                             @Param("lastLoginTime") LocalDateTime lastLoginTime,
                             @Param("lastLoginIp") String lastLoginIp);

    /**
     * 更新用户状态
     */
    void updateStatus(@Param("id") Long id,
                      @Param("status") String status,
                      @Param("updateBy") String updateBy);

    /**
     * 查询不活跃用户
     */
    List<User> selectInactiveUsers(@Param("thresholdDate") LocalDateTime thresholdDate,
                                   @Param("limit") Integer limit);
}


