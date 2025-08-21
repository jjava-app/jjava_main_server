package org.example.jjava_main.dto;

import lombok.Data;

public class HomeResponse {
    @Data
    public static class DTO {
        private UserResponse.DTO userInfo;
        private LeaderboardResponse.DTO leaderboard;
        private QuestionResponse.HomeDTO sqList;

        public DTO(UserResponse.DTO userDTO, LeaderboardResponse.DTO leaderboardDTO, QuestionResponse.HomeDTO sqDTO) {
            this.userInfo = userDTO;
            this.leaderboard = leaderboardDTO;
            this.sqList = sqDTO;
        }
    }
}
