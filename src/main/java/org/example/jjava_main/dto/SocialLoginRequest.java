package org.example.jjava_main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class SocialLoginRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // @NoArgsConstructor @AllArgsConstructor : @RequestBody 역직렬화를 위한 기본 생성자 제공(없으면 500) + 테스트/수동 생성 편의용 전체 필드 생성자 제공

    public static class LoginDTO {
        private String accessToken;
    }
}