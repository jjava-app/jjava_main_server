package org.example.jjava_main.domain.auth;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.JwtUtil;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.dto.SocialLoginResponse;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.GoogleUserInfo;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.KakaoMeResponse;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.NaverMeResponse;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.NaverUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    // ---------- NAVER ----------
    @Transactional
    public SocialLoginResponse.LoginDTO naverOauthLogin(String accessToken) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<NaverMeResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, request, NaverMeResponse.class
        );

        NaverUser nu = Optional.ofNullable(resp.getBody())
                .map(NaverMeResponse::getResponse)
                .orElseThrow(() -> new RuntimeException("Naver response empty"));

        String providerId = nu.getId(); // 문자열
        String email = nu.getEmail();
        String nickName = (nu.getName() != null && !nu.getName().isBlank())
                ? nu.getNickname() : (nu.getNickname() != null ? nu.getNickname() : "네이버사용자");

        User user = findOrCreateUser(nickName, email);

        return toLoginResponse(user);
    }

    // ---------- KAKAO ----------
    @Transactional
    public SocialLoginResponse.LoginDTO kakaoOauthLogin(String accessToken) {
        // 1) 카카오 유저 조회
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<KakaoMeResponse> resp =
                restTemplate.exchange(url, HttpMethod.GET, req, KakaoMeResponse.class);

        KakaoMeResponse me = Optional.ofNullable(resp.getBody())
                .orElseThrow(() -> new RuntimeException("Kakao response empty"));

        String providerId = String.valueOf(me.getId());
        String email = (me.getKakaoAccount() != null) ? me.getKakaoAccount().getEmail() : null;
        String nickname = (me.getKakaoAccount() != null && me.getKakaoAccount().getProfile() != null)
                ? me.getKakaoAccount().getProfile().getNickname()
                : "카카오사용자";

        User user = findOrCreateUser(nickname, email);
        return toLoginResponse(user);
    }

    // ---------- GOOGLE ----------
    @Transactional
    public SocialLoginResponse.LoginDTO googleOauthLogin(String accessToken) {
        // 1) 구글 userinfo 호출
        String url = "https://openidconnect.googleapis.com/v1/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfo> resp =
                restTemplate.exchange(url, HttpMethod.GET, req, GoogleUserInfo.class);
        GoogleUserInfo u = Optional.ofNullable(resp.getBody())
                .orElseThrow(() -> new RuntimeException("Google userinfo empty"));

        String providerId = u.getSub(); // 고유 ID
        String email = u.getEmail();    // null 가능
        String nickName = (u.getName() != null && !u.getName().isBlank()) ? u.getName() : "Google사용자";
        User user = findOrCreateUser(nickName, email);
        return toLoginResponse(user);
    }

    //공통모듈

    private User findOrCreateUser(String username, String email) {
        // 1) 같은 소셜로 재로그인: 고정 username으로 먼저 탐색
        var u = userRepository.findByUsername(username).orElse(null);
        if (u != null) return u;

        // 2) 이메일 동의한 경우 기존 계정과 연결
        if (email != null && !email.isBlank()) {
            u = userRepository.findByEmail(email).orElse(null);
            if (u != null) return u;
        }

        // 3) 신규 생성 (빌더 공통 사용)
        u = buildNewUser(username, email);
        userRepository.save(u);
        return u;
    }

    private User buildNewUser(String username, String email) {
        return User.builder()
                .username(username) // 고유/재현 가능한 값 (예: NAVER_123..., KAKAO_..., GOOGLE_...)
                .password(BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()))
                .email(email)        // null 허용
                .role(UserRole.USER)
                .build();
    }

    private SocialLoginResponse.LoginDTO toLoginResponse(User user) {
        String jwt = JwtUtil.create(user);
        // 혹시 유틸이 "Bearer xxx"로 반환하면 접두어 제거
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring("Bearer ".length());
        }
        return SocialLoginResponse.LoginDTO.builder()
                .accessToken(jwt)                          // 순수 JWT
                .user(SocialLoginResponse.UserDTO.of(user))// id/email/username/role 만
                .build();
    }
}
