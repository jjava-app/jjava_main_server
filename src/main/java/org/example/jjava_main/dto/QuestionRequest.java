package org.example.jjava_main.dto;

import lombok.Data;
import org.example.jjava_main.domain.question.QuestionType;

public class QuestionRequest {
    @Data
    public static class DTO {
        private String type;
        private String title;
        private String content;
        private String testVariable;
        private String testAnswer;
    }
}
