package org.example.jjava_main.dto;

import lombok.Data;

public class QuestionRequest {

    @Data
    public static class SolvedQuestionCreateDTO {
        private Integer userId;
        private Integer questionId;
        private String serializedJson;
        private String blockExtensionJson;

        public SolvedQuestionCreateDTO(Integer userId, Integer questionId, String serializedJson, String blockExtensionJson) {
            this.userId = userId;
            this.questionId = questionId;
            this.serializedJson = serializedJson;
            this.blockExtensionJson = blockExtensionJson;
        }
    }
}
