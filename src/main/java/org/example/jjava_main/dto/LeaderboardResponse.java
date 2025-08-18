package org.example.jjava_main.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public class LeaderboardResponse {
    @Data
    public static class ItemDTO {
        private Integer userId;
        private String username;
        private Integer score;
        private Integer rank;

        @Builder
        public ItemDTO(Integer userId, String username, Integer score, Integer rank) {
            this.userId = userId;
            this.username = username;
            this.score = score;
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
