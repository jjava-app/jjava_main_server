package org.example.jjava_main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public class CheckResponse {

    @Data
    @NoArgsConstructor
    public static class PassDTO {
        private Integer userId;
        private Integer questionId;
        private boolean passed; // 성공, 실패 여부
        private String message;      // "정답입니다." / "오답입니다."
        private String result;          // 실제 출력
        private String code;  // 사용자 코드
        // AI 리팩토링 결과
        private String refactoredCode;   // 리팩토링된 코드
        private String refactorNote;     // 설명

        public PassDTO(Integer userId, Integer questionId, boolean passed, String message, String result, String code, String refactoredCode, String refactorNote) {
            this.userId = userId;
            this.questionId = questionId;
            this.passed = passed;
            this.message = message;
            this.result = result;
            this.code = code;
            this.refactoredCode = refactoredCode;
            this.refactorNote = refactorNote;
        }
    }

    @Data
    @NoArgsConstructor
    public static class FailDTO {
        private Integer userId;
        private Integer questionId;
        private boolean passed; // 성공, 실패 여부
        private Integer index;                  // 테스트 인덱스(0부터)
        private Map<String, Object> failedInputs;  // testVariable 원본
        private String message;      // "정답입니다." / "오답입니다."
        private String expected;            // 기대값
        private String result;              // 실제 출력

        public FailDTO(Integer userId, Integer questionId, boolean passed, Integer index, Map<String, Object> failedInputs, String message, String expected, String result) {
            this.userId = userId;
            this.questionId = questionId;
            this.passed = passed;
            this.index = index;
            this.failedInputs = failedInputs;
            this.message = message;
            this.expected = expected;
            this.result = result;
        }
    }
}
