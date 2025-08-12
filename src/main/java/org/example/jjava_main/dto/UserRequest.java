package org.example.jjava_main.dto;


import lombok.Data;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;

public class UserRequest {

    @Data
    public static class LevelUpdateDTO {
        private UserLevel level;
        private String username;

        public LevelUpdateDTO(UserLevel level, String username) {
            this.level = level;
            this.username = username;
        }
    }

    //유저수정 - Min
    @Data
    public static class UserUpdateDTO{
        private String username;
        private String email;
        private UserRole role;
        private Integer score;

        public UserUpdateDTO(String username, String email, UserRole role, Integer score) {
            this.username = username;
            this.email = email;
            this.role = role;
            this.score = score;
        }
    }
}
