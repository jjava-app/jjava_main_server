package org.example.jjava_main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

public class CompileRequest {

    @Data
    @NoArgsConstructor // 기본 생성자 추가 (Jackson 역직렬화용)
    public static class DTO {
        private String payload;

        public DTO(String payload) {
            this.payload = payload;
        }
    }
}
