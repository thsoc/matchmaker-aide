package com.aide.context;


import com.aide.entity.DO.UserDo;

/**
 * @author 20721
 * @description 用户上下文
 * @date 2026/5/18
 * @date 10:48
 */
public class UserContext {
    private static final ThreadLocal<UserDo> userHolder = new ThreadLocal<>();

    public static void setUser(UserDo user) {
        userHolder.set(user);
    }

    public static UserDo getUser() {
        return userHolder.get();
    }

    public static Long getUserId() {
        UserDo user = userHolder.get();
        return user != null ? user.getId() : null;
    }

    public static String getAccount() {
        UserDo user = userHolder.get();
        return user != null ? user.getAccount() : null;
    }

    public static void clear() {
        userHolder.remove();
    }
}
