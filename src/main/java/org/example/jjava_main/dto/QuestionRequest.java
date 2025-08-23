package org.example.jjava_main.dto;

import lombok.Data;

public class QuestionRequest {

    @Data
    public static class SolvedQuestionCreateDTO {
        private Integer questionId;
        private String serializedJson;
        private String blockExtensionJson;

        public SolvedQuestionCreateDTO(Integer questionId, String serializedJson, String blockExtensionJson) {
            this.questionId = questionId;
            this.serializedJson = serializedJson;
            this.blockExtensionJson = blockExtensionJson;
        }
    }

    @Data
    public static class DTO {
        private String type;
        private String title;
        private String content;
        private String testVariable;
        private String testAnswer;
    }
}
