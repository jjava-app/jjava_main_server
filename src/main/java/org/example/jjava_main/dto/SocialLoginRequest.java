package org.example.jjava_main.dto;


import lombok.Data;


public class SocialLoginRequest {

    @Data
    public static class LoginDTO {
        private String accessToken;

        public LoginDTO(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}