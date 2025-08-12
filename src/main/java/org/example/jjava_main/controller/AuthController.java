package org.example.jjava_main.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.auth.AuthService;
import org.example.jjava_main.domain.user.UserService;
import org.example.jjava_main.dto.SocialLoginRequest;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;


    // 네이버 oauth 로그인
    @PostMapping("/login/naver")
    public ResponseEntity<?> naverOauthLogin(@RequestBody SocialLoginRequest.LoginDTO reqDTO) {
        var respDTO = authService.naverOauthLogin(reqDTO.getAccessToken());
        return Resp.ok(respDTO);
    }

    // 카카오 oauth 로그인
    @PostMapping("/login/kakao")
    public ResponseEntity<?> kakaoOauthLogin(@RequestBody SocialLoginRequest.LoginDTO reqDTO) {
        var respDTO = authService.kakaoOauthLogin(reqDTO.getAccessToken());
        return Resp.ok(respDTO);
    }

    // 구글 oauth 로그인
    @PostMapping("/login/google")
    public ResponseEntity<?> googleOauthLogin(@RequestBody SocialLoginRequest.LoginDTO reqDTO) {
        var respDTO = authService.googleOauthLogin(reqDTO.getAccessToken());
        return Resp.ok(respDTO);
    }

    // 이메일 검증 어노테이션 @Email 이메일 검증 어노테이션
    @GetMapping("/auth/email/check/{email}")
    public ResponseEntity<?> verificationEmail(@PathVariable("email") @Email String email) {
        UserResponse.CheckEmailDTO respDTO = authService.verificationEmail(email);
        return Resp.ok(respDTO);
    }

    // 닉네임 중복여부 체크 검사
    @GetMapping("/auth/nickname/check/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable("nickname") String nickname) {
        UserResponse.CheckNicknameDTO respDTO = authService.isNicknameAvailable(nickname);
        return Resp.ok(respDTO);
    }

    // 이메일 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinDTO reqDTO) {
        UserResponse.JoinDTO respDTO = authService.createUser(reqDTO);
        return Resp.ok(respDTO);
    }

    // 이메일 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO reqDTO, HttpServletRequest request) {
        UserResponse.LoginDTO respDTO = authService.emailLogin(reqDTO);
        HttpSession session = request.getSession(true); // ← 세션 생성
        session.setAttribute("USER_ID", respDTO.getId());
        return Resp.ok(respDTO);
    }
}
