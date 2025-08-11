package org.example.jjava_main.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.auth.AuthService;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;
    private final HttpSession session;

    /**
     * @Email 이메일 검증 어노테이션
     */
    @GetMapping("/auth/email/check/{email}")
    public ResponseEntity<?> verificationEmail(@PathVariable("email") @Email String email) {
        UserResponse.CheckEmailDTO respDTO = authService.verificationEmail(email);
        return Resp.ok(respDTO);
    }

    /**
     * 닉네임 중복여부 체크 검사
     */
    @GetMapping("/auth/nickname/check/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable("nickname") String nickname) {
        UserResponse.CheckNicknameDTO respDTO = authService.isNicknameAvailable(nickname);
        return Resp.ok(respDTO);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinDTO reqDTO) {
        UserResponse.JoinDTO respDTO = authService.createUser(reqDTO);
        return Resp.ok(respDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO reqDTO) {
        UserResponse.LoginDTO respDTO = authService.login(reqDTO);
        return Resp.ok(respDTO);
    }
}
