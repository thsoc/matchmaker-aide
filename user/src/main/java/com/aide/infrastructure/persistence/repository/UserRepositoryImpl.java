package com.aide.infrastructure.persistence.repository;

/**
 * @author mazg
 * @description 负责领域对象与持久化实体的转换
 *
 * 职责：
 * 1. 实现领域层定义的 Repository 接口
 * 2. 处理 UserDo ↔ User 的转换
 * 3. 调用 MyBatis-Plus Mapper 进行数据库操作
 * @date 2026/5/28
 * @date 11:55
 */

import com.aide.domain.model.UserDo;
import com.aide.domain.repository.UserRepository;
import com.aide.infrastructure.persistence.entity.User;
import com.aide.infrastructure.persistence.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public UserDo findById(Long id) {
        User user = userMapper.selectById(id);

        if (user == null || user.getDelFlag() == 1) {
            return null;
        }

        return convertToDomainObject(user);
    }

    @Override
    public UserDo findByAccount(String account) {
        User user = userMapper.selectByAccount(account);

        if (user == null) {
            return null;
        }

        return convertToDomainObject(user);
    }

    @Override
    public UserDo findByMobile(String mobile) {
        User user = userMapper.selectByMobile(mobile);

        if (user == null) {
            return null;
        }

        return convertToDomainObject(user);
    }

    @Override
    public void save(UserDo userDo) {
        User user = convertToEntity(userDo);

        if (userDo.getId() == null) {
            userMapper.insert(user);
            log.debug("新增用户，账号: {}", userDo.getAccount());
        } else {
            userMapper.updateById(user);
            log.debug("更新用户，账号: {}", userDo.getAccount());
        }
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        userMapper.updateAvatarById(avatarUrl, userId);
        log.debug("更新用户头像，用户ID: {}", userId);
    }

    @Override
    public void updateMobile(Long userId, String mobile) {
        UserDo userDo = findById(userId);
        if (userDo != null) {
            userDo.bindMobile(mobile);
            User user = convertToEntity(userDo);
            userMapper.updateMobileById(userDo);
            log.debug("更新用户手机号，用户ID: {}", userId);
        }
    }

    @Override
    public IPage<User> getPageUsers(int current, int size, User user, String startTime, String endTime) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(User::getDelFlag, 0)
                .like(StringUtils.hasText(user.getUsername()), User::getUsername, user.getUsername())
                .eq(StringUtils.hasText(user.getMobile()), User::getMobile, user.getMobile())
                .eq(StringUtils.hasText(user.getEmail()), User::getEmail, user.getEmail())
                .eq(StringUtils.hasText(user.getStatus()), User::getStatus, user.getStatus())
                .eq(StringUtils.hasText(user.getRole()), User::getRole, user.getRole())
                .eq(StringUtils.hasText(user.getSex()), User::getSex, user.getSex());

        if (StringUtils.hasText(startTime)) {
            wrapper.ge(User::getCreateTime, startTime);
        }
        if (StringUtils.hasText(endTime)) {
            wrapper.le(User::getCreateTime, endTime);
        }

        wrapper.orderByDesc(User::getCreateTime);

        return userMapper.selectPage(page, wrapper);
    }

    /**
     * @author mazg
     * @description 根据ID 查询用户
     * @date 12:37 2026/5/28
     * @return
     **/
    @Override
    public User getUserById(Long id) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id);
        return userMapper.selectOne(wrapper);
    }

    /**
     * @author mazg
     * @description 更新用户
     * @date 12:37 2026/5/28
     * @return
     **/
    @Override
    public boolean deleteUserById(Long userId) {
        if (userId == null) {
            log.warn("删除用户失败：用户ID为空");
            return false;
        }

        boolean result = new LambdaUpdateChainWrapper<>(userMapper)
                .eq(User::getId, userId)
                .set(User::getDelFlag, 1)
                .set(User::getUpdateTime, LocalDateTime.now())
                .update();

        if (result) {
            log.info("逻辑删除用户成功，用户ID: {}", userId);
        } else {
            log.warn("逻辑删除用户失败，用户可能不存在，用户ID: {}", userId);
        }
        return result;
    }

    /**
     * 持久化实体 → 领域对象
     */
    private UserDo convertToDomainObject(User entity) {
        return UserDo.builder()
                .id(entity.getId())
                .account(entity.getAccount())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .description(entity.getDescription())
                .introduce(entity.getIntroduce())
                .role(entity.getRole())
                .status(entity.getStatus())
                .sex(entity.getSex())
                .avatar(entity.getAvatar())
                .email(entity.getEmail())
                .mobile(entity.getMobile())
                .birthday(entity.getBirthday())
                .income(entity.getIncome())
                .occupation(entity.getOccupation())
                .integral(entity.getIntegral())
                .loginCount(entity.getLoginCount())
                .lastLoginTime(entity.getLastLoginTime())
                .lastLoginIp(entity.getLastLoginIp())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .delFlag(entity.getDelFlag())
                .createBy(entity.getCreateBy())
                .updateBy(entity.getUpdateBy())
                .version(entity.getVersion())
                .build();
    }

    /**
     * 领域对象 → 持久化实体
     */
    private User convertToEntity(UserDo domainObject) {
        return User.builder()
                .id(domainObject.getId())
                .account(domainObject.getAccount())
                .username(domainObject.getUsername())
                .password(domainObject.getPassword())
                .description(domainObject.getDescription())
                .introduce(domainObject.getIntroduce())
                .role(domainObject.getRole())
                .status(domainObject.getStatus())
                .sex(domainObject.getSex())
                .avatar(domainObject.getAvatar())
                .email(domainObject.getEmail())
                .mobile(domainObject.getMobile())
                .birthday(domainObject.getBirthday())
                .income(domainObject.getIncome())
                .occupation(domainObject.getOccupation())
                .integral(domainObject.getIntegral())
                .loginCount(domainObject.getLoginCount())
                .lastLoginTime(domainObject.getLastLoginTime())
                .lastLoginIp(domainObject.getLastLoginIp())
                .createTime(domainObject.getCreateTime())
                .updateTime(domainObject.getUpdateTime())
                .delFlag(domainObject.getDelFlag())
                .createBy(domainObject.getCreateBy())
                .updateBy(domainObject.getUpdateBy())
                .version(domainObject.getVersion())
                .build();
    }
}
