package org.example.jjava_main._core.util;

import org.example.jjava_main.domain.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }
        return (User) authentication.getPrincipal();
    }

    public static Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
