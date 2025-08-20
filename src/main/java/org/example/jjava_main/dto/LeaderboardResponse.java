package org.example.jjava_main.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public class LeaderboardResponse {
    @Data
    public static class ItemDTO {
        private Integer userId;
        private String username;
        private Integer currentScore; // after_score
        private Integer delta;        // delta_score
        private Integer rank;

        @Builder
        public ItemDTO(Integer userId, String username, Integer currentScore, Integer delta, Integer rank) {
            this.userId = userId;
            this.username = username;
            this.currentScore = currentScore;
            this.delta = delta;
            this.rank = rank;
        }
    }

    @Data
    public static class DTO {
        private List<ItemDTO> rankingList;

        @Builder
        public DTO(List<ItemDTO> rankingList) {
            this.rankingList = rankingList;
        }
    }
}
