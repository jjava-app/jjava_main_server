package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.auth.AuthService;
import org.example.jjava_main.dto.SocialLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;


    // 네이버 oauth 로그인
    @PostMapping("/login/naver")
    public ResponseEntity<?> naverOauthLogin(@RequestBody SocialLoginRequest.LoginDTO reqDTO) {
        var respDTO = authService.naverOauthLogin(reqDTO.getAccessToken(), reqDTO.getFcmToken());
        return Resp.ok(respDTO);
    }
}
