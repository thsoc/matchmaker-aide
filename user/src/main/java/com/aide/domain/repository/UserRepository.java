package com.aide.domain.repository;

import com.aide.domain.model.UserDo;
import com.aide.infrastructure.persistence.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author mazg
 * @description 用户仓储接口 - 定义用户领域对象的持久化契约
 *
 * 职责：
 * 1. 提供用户的查询和保存能力
 * 2. 隐藏基础设施层的实现细节
 * 3. 领域服务只依赖此接口，不直接操作 Mapper
 *
 * @date 2026/5/28
 */
public interface UserRepository {

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户领域对象，不存在时返回 null
     */
    UserDo findById(Long id);

    /**
     * 根据账号查询用户
     *
     * @param account 账号
     * @return 用户领域对象，不存在时返回 null
     */
    UserDo findByAccount(String account);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户领域对象，不存在时返回 null
     */
    UserDo findByMobile(String mobile);

    /**
     * 保存用户（新增或更新）
     *
     * @param userDo 用户领域对象
     * @return
     */
    UserDo save(UserDo userDo);

    /**
     * 更新用户头像
     *
     * @param userId 用户 ID
     * @param avatarUrl 头像 URL
     */
    void updateAvatar(Long userId, String avatarUrl);

    /**
     * 更新用户手机号
     *
     * @param userId 用户 ID
     * @param mobile 手机号
     */
    void updateMobile(Long userId, String mobile);

    /**
     * 分页查询用户列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param user 查询条件
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户列表
     */
    IPage<User> getPageUsers(int current, int size, User user, String startTime, String endTime);

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户
     */
    User getUserById(Long id);

    /**
     * 删除
     *
     * @param id 用户
     * @return 是否更新成功
     */
    boolean deleteUserById(Long id);
}
