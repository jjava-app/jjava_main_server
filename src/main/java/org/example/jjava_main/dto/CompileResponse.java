package org.example.jjava_main.dto;

import lombok.Data;

public class CompileResponse {

    @Data
    public static class DTO {
        private Integer userId;
        private String code;
        private String result;

        public DTO(Integer userId, String code, String result) {
            this.userId = userId;
            this.code = code;
            this.result = result;
        }
    }

}
