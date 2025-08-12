package org.example.jjava_main.dto;

import lombok.Data;

import java.util.List;

public class QuestionResponse {

    @Data
    public static class ListDTO {
        private Integer userId;
        private Integer totalCount;
        private Integer solvedCount;
        private List<QuestionDTO> questions;
        private List<Integer> solvedQuestionIds;

        @Data
        public static class QuestionDTO {
            private Integer questionId;
            private String questionType;
            private String title;

            public QuestionDTO(Integer questionId, String questionType, String title) {
                this.questionId = questionId;
                this.questionType = questionType;
                this.title = title;
            }
        }

        public ListDTO(Integer userId, Integer totalCount, Integer solvedCount, List<QuestionDTO> questions, List<Integer> solvedQuestionIds) {
            this.userId = userId;
            this.totalCount = totalCount;
            this.solvedCount = solvedCount;
            this.questions = questions;
            this.solvedQuestionIds = solvedQuestionIds;
        }
    }

    @Data
    public static class DetailDTO {
        private Integer questionId;
        private String title;
        private String content;

        public DetailDTO(Integer questionId, String title, String content) {
            this.questionId = questionId;
            this.title = title;
            this.content = content;
        }
    }
}

