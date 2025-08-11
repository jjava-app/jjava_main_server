package org.example.jjava_main.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.jjava_main.domain.user.User;

public class SocialLoginResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginDTO {
        private String accessToken;
        private UserDTO user;
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