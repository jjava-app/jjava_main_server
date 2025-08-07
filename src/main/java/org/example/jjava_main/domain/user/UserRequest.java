package org.example.jjava_main.domain.user;

import lombok.Data;

public class UserRequest {

    @Data
    public static class UpdateLevelDTO {
        private UserLevel level;

        public UpdateLevelDTO(UserLevel level) {
            this.level = level;
        }
    }
}


