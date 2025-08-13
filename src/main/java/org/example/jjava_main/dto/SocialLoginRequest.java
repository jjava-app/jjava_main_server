package org.example.jjava_main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class SocialLoginRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String accessToken;
    }
}