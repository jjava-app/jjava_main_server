package org.example.jjava_main.dto;

import lombok.Data;

public class HomeResponse {
    @Data
    public static class DTO {
        private UserResponse.DTO userDTO;
        private LeaderboardResponse.DTO leaderboardDTO;
        private QuestionResponse.HomeDTO sqDTO;

        public DTO(UserResponse.DTO userDTO, LeaderboardResponse.DTO leaderboardDTO, QuestionResponse.HomeDTO sqDTO) {
            this.userDTO = userDTO;
            this.leaderboardDTO = leaderboardDTO;
            this.sqDTO = sqDTO;
        }
    }
}
