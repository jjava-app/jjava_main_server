package org.example.jjava_main.domain.auth;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.JwtUtil;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.dto.SocialLoginResponse;
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
    public Object naverOauthLogin(String accessToken, String fcmToken) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<NaverMeResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, request, NaverMeResponse.class
        );

        var u = Optional.ofNullable(resp.getBody())
                .map(NaverMeResponse::response)
                .orElseThrow(() -> new RuntimeException("Naver response empty"));

        String providerId = u.id(); // 문자열
        String email = u.email();
        String displayName = (u.name() != null && !u.name().isBlank())
                ? u.name() : (u.nickname() != null ? u.nickname() : "네이버사용자");

        User user = findOrCreateUser("NAVER_" + providerId, email, displayName);

        return toLoginResponse(user);

    }

    public static record NaverMeResponse(String resultcode, String message, NaverUser response) {
    }

    public static record NaverUser(
            String id,
            String email,
            String name,
            String nickname,
            String profile_image,
            String mobile,
            String birthyear,
            String birthday,
            String gender
    ) {
    }

    // ---------- KAKAO ----------
    @Transactional
    public Object kakaoOauthLogin(String accessToken, String fcmToken) {
        // 1) 카카오 유저 조회
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<KakaoMeResponse> resp =
                restTemplate.exchange(url, HttpMethod.GET, req, KakaoMeResponse.class);

        KakaoMeResponse me = Optional.ofNullable(resp.getBody())
                .orElseThrow(() -> new RuntimeException("Kakao response empty"));

        String providerId = String.valueOf(me.id());
        String email = (me.kakao_account() != null) ? me.kakao_account().email() : null;
        String nickname = (me.kakao_account() != null && me.kakao_account().profile() != null)
                ? me.kakao_account().profile().nickname()
                : "카카오사용자";

        // 2) 빠른 upsert (username을 고정 패턴으로 -> 재로그인 매칭 안정)
        User user = findOrCreateUser("KAKAO_" + providerId, email, nickname);

        // TODO: fcmToken 저장/업데이트 필요하면 여기서 처리

        // 3) 내 JWT 발급
        return toLoginResponse(user);
    }

    public static record KakaoMeResponse(long id, KakaoAccount kakao_account) {
    }

    public static record KakaoAccount(String email, KakaoProfile profile) {
    }

    public static record KakaoProfile(String nickname, String profile_image_url) {
    }

    // ---------- GOOGLE ----------
    @Transactional
    public Object googleOauthLogin(String accessToken, String fcmToken) {
        // 1) 구글 userinfo 호출
        String url = "https://openidconnect.googleapis.com/v1/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfo> resp =
                restTemplate.exchange(url, HttpMethod.GET, req, GoogleUserInfo.class);
        GoogleUserInfo u = Optional.ofNullable(resp.getBody())
                .orElseThrow(() -> new RuntimeException("Google userinfo empty"));

        // 2) 식별/표시 정보
        String providerId = u.sub(); // 고유 ID
        String email = u.email();    // null 가능
        String name = (u.name() != null && !u.name().isBlank()) ? u.name() : "Google사용자";

        // 3) upsert (username을 고정 패턴으로)
        User user = findOrCreateUser("GOOGLE_" + providerId, email, name);

        // TODO: fcmToken 저장/갱신 필요하면 여기서 처리

        // 4) 내 JWT 발급
        return toLoginResponse(user);
    }

    // 구글 userinfo DTO
    public static record GoogleUserInfo(
            String sub,
            String email,
            Boolean email_verified,
            String name,
            String picture
    ) {
    }

    //공통모듈

    private User findOrCreateUser(String fixedUsername, String email, String displayName) {
        // 1) 같은 소셜로 재로그인: 고정 username으로 먼저 탐색
        var u = userRepository.findByUsername(fixedUsername).orElse(null);
        if (u != null) return u;

        // 2) 이메일 동의한 경우 기존 계정과 연결
        if (email != null && !email.isBlank()) {
            u = userRepository.findByEmail(email).orElse(null);
            if (u != null) return u;
        }

        // 3) 신규 생성 (빌더 공통 사용)
        u = buildNewUser(fixedUsername, email, displayName);
        userRepository.save(u);
        return u;
    }

    private User buildNewUser(String username, String email, String displayName) {
        return User.builder()
                .username(username) // 고유/재현 가능한 값 (예: NAVER_123..., KAKAO_..., GOOGLE_...)
                .password(BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()))
                .email(email)        // null 허용
                .role(UserRole.USER)
                // .displayName(displayName) // 엔티티에 필드 있으면 여기서 세팅
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
