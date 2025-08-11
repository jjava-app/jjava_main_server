package org.example.jjava_main.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class NaverMeResponse {
            private String resultcode;
            private String message;
            private NaverUser response;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class NaverUser {
            private String id;
            private String email;
            private String name;
            private String nickname;
            @JsonProperty("profile_image")
            private String profileImage;
            private String mobile;
            private String birthyear;
            private String birthday;
            private String gender;
        }

        // ----- KAKAO -----
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class KakaoMeResponse {
            private long id;
            @JsonProperty("kakao_account")
            private KakaoAccount kakaoAccount;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class KakaoAccount {
            private String email;
            private KakaoProfile profile;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class KakaoProfile {
            private String nickname;
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }

        // ----- GOOGLE -----
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class GoogleUserInfo {
            private String sub;
            private String email;
            @JsonProperty("email_verified")
            private Boolean emailVerified;
            private String name;
            private String picture;
        }
    }
}