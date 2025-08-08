package org.example.jjava_main.domain.user;

import lombok.Data;

public class UserRequest {

    @Data
    public static class LevelUpdateDTO {
        private UserLevel level;

        public LevelUpdateDTO(UserLevel level) {
            this.level = level;
        }
    }
}


