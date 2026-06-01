package com.aide.common.auth.context;


import com.aide.common.auth.entity.UserInfo;

/**
 * @author mazg
 * @description 用户上下文
 * @date 2026/5/18
 * @date 10:48
 */
public class UserContext {
    private static final ThreadLocal<UserInfo> userHolder = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        userHolder.set(user);
    }

    public static UserInfo getUser() {
        return userHolder.get();
    }

    public static Long getUserId() {
        UserInfo user = userHolder.get();
        return user != null ? user.getId() : null;
    }

    public static String getAccount() {
        UserInfo user = userHolder.get();
        return user != null ? user.getAccount() : null;
    }

    public static void clear() {
        userHolder.remove();
    }
}
