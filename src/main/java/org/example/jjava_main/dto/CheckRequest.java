package org.example.jjava_main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class CheckRequest {

    @Data
    public static class DTO {
        private String type;
        private String payload;
        private List<TestSpecDTO> tests;

        @Data
        public static class TestSpecDTO {
            private Map<String, Object> testVariable;
            private String testAnswer;

            public TestSpecDTO(Map<String, Object> testVariable, String testAnswer) {
                this.testVariable = testVariable;
                this.testAnswer = testAnswer;
            }
        }


        public DTO(String type, String payload, List<TestSpecDTO> tests) {
            this.type = type;
            this.payload = payload;
            this.tests = tests;
        }
    }

    @Data
    @NoArgsConstructor
    public static class RefactorDTO {
        private String model = "gpt-3.5-turbo";
        private List<Message> messages;
        private double temperature = 0.5;

        @Data
        public static class Message {
            private String role;
            private String content;
        }

        public RefactorDTO(String model, List<Message> messages, double temperature) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
        }
    }
}
