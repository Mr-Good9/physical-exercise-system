package com.good.physicalexercisesystem.utils;

import com.good.physicalexercisesystem.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContext {
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        User user = userThreadLocal.get();
        if (user == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                // 这里可以通过 UserService 获取完整的用户信息
                // 暂时返回 null，等需要的时候再实现
                return null;
            }
        }
        return user;
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
