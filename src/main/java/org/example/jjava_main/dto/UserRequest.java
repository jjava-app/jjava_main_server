package org.example.jjava_main.dto;


import lombok.Data;
import org.example.jjava_main.domain.user.UserLevel;

public class UserRequest {

    @Data
    public static class LevelUpdateDTO {
        private UserLevel level;

        public LevelUpdateDTO(UserLevel level) {
            this.level = level;
        }
    }
}
