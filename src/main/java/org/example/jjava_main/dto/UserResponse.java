package org.example.jjava_main.dto;

import lombok.Data;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;

public class UserResponse {

    @Data
    public static class CheckNicknameDTO {
        private Boolean available;

        public CheckNicknameDTO(Boolean available) {
            this.available = available;
        }
    }

    @Data
    public static class CheckEmailDTO {
        private Boolean verified;

        public CheckEmailDTO(Boolean verified) {
            this.verified = verified;
        }
    }

    @Data
    public static class JoinDTO {
        private String accessToken;
        private String email;
        private String nickname;
        private UserLevel level;
        private UserRole role;

        public JoinDTO(User user, String accessToken) {
            this.accessToken = accessToken;
            this.email = user.getEmail();
            this.nickname = user.getUsername();
            this.level = user.getLevel();
            this.role = user.getRole();
        }
    }

    @Data
    public static class LoginDTO {
        private String accessToken;
        private String email;
        private String nickname;
        private UserLevel level;
        private UserRole role;
        private Integer score;

        public LoginDTO(User user, String accessToken) {
            this.accessToken = accessToken;
            this.email = user.getEmail();
            this.nickname = user.getUsername();
            this.level = user.getLevel();
            this.role = user.getRole();
            this.score = user.getScore();
        }
    }
}
