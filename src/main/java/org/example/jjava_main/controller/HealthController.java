package org.example.jjava_main.controller;

import jakarta.servlet.http.HttpSession;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "<h1>Hello World!</h1>";
    }

    @GetMapping("/admin/health")
    public String adminHealth() {
        return "<h1>Hello World!</h1>";
    }

    @GetMapping("/session")
    public ResponseEntity<?> healthCheck(HttpSession session) {
        // ✅ 가짜 유저 생성
        User mockUser = User.builder()
                .id(999)
                .email("fake@user.com")
                .password("1234")
                .username("FakeUser")
                .level(UserLevel.BEGINNER)
                .role(UserRole.USER)
                .score(0)
                .build();

        // ✅ Spring Security의 Authentication 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());

        // ✅ SecurityContext에 인증 정보 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ 현재 세션 ID 확인
        String sessionId = session.getId();

        return Resp.ok("Health Check 성공 - SessionID: " + sessionId);
    }
}
