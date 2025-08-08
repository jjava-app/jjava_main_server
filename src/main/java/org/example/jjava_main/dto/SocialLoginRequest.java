package org.example.jjava_main.dto;


import lombok.Data;


public class SocialLoginRequest {

    @Data
    public static class LoginDTO {
        private String accessToken;
        private String fcmToken;

        public LoginDTO(String accessToken, String fcmToken) {
            this.accessToken = accessToken;
            this.fcmToken = fcmToken;
        }
    }
}