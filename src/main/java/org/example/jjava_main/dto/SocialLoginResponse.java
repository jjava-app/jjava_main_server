package org.example.jjava_main.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.jjava_main.domain.user.User;

import java.util.List;

public class SocialLoginResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginDTO {
        private String accessToken;
        private UserDTO user;

        private List<LinkedAccountDTO> linked;
    }

    @Data
    public static class LinkedAccountDTO {
        private final String provider; // 'naver'|'google'|'kakao' (원하면 'local'도)
        private final String email;    // null 방지 위해 빈 문자열로 넣어줄 것
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDTO {
        private Integer id;          // ← User의 PK 타입에 맞춰서 Integer/Long 조정
        private String email;
        private String username;
        private String role;

        public static UserDTO of(User u) {
            return UserDTO.builder()
                    .id(u.getId())
                    .email(u.getEmail())
                    .username(u.getUsername())
                    .role(u.getRole().name())
                    .build();
        }
    }

    // ========== 외부 OAuth API DTO 묶음 ==========
    public static final class OAuth {

        private OAuth() {
        }

        // ----- NAVER -----
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NaverMeResponse {
            private String resultcode;
            private String message;
            private NaverUser response;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NaverUser {
            private String id;
            private String email;
            private String name;
            private String nickname;
        }

        // ----- KAKAO -----
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KakaoMeResponse {
            private long id;

            @JsonProperty("kakao_account") //JSON kakao_account 매핑용
            private KakaoAccount kakaoAccount;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KakaoAccount {
            private String email;
            private KakaoProfile profile;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KakaoProfile {
            private String nickname;
        }

        // ----- GOOGLE -----
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GoogleUserInfo {
            private String sub;
            private String email;
            private String name;
        }
    }
}