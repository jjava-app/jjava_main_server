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
}