package org.example.jjava_main.domain.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.error.ex.Exception400;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main._core.util.EmailCode;
import org.example.jjava_main._core.util.JwtUtil;
import org.example.jjava_main._core.util.PrincipalDetails;
import org.example.jjava_main.domain.auth.provider.*;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.dto.SocialLoginResponse;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.GoogleUserInfo;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.KakaoMeResponse;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.NaverMeResponse;
import org.example.jjava_main.dto.SocialLoginResponse.OAuth.NaverUser;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAccountProviderRepository uapRepository;
    private final ProviderRepository providerRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${resend.api-key}")
    private String apiKey;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;


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

        NaverUser n = Optional.ofNullable(resp.getBody())
                .map(NaverMeResponse::getResponse)
                .orElseThrow(() -> new RuntimeException("Naver response empty"));

        String providerUserId = n.getId();
        String providerEmail = n.getEmail();
        String nickName = (n.getName() != null && !n.getName().isBlank())
                ? n.getName() : (n.getNickname() != null ? n.getNickname() : "네이버사용자");

        Provider naver = providerRepository.findByProviderType(ProviderType.NAVER)
                .orElseThrow(() -> new IllegalStateException("Provider NAVER not seeded"));


        // (A) (provider, providerId) 매핑 존재 여부 확인
        var linkOpt = uapRepository.findLink(ProviderType.NAVER, providerUserId);

        User user;

        if (linkOpt.isPresent()) {
            // 이미 연결된 유저면 그 유저로 로그인
            var link = linkOpt.get();
            user = link.getUser();
            link.updateEmailIfChanged(providerEmail);
            log.info("[네이버] 계정 로그인 성공 — 기존 연동을 재사용합니다. -> userId={}, providerUserId={}", user.getId(), n.getId());
        } else {
            // (B) 없으면 기존 로직으로 유저 생성
            user = findOrCreateUser(nickName, providerEmail); // 네가 쓰던 생성 로직 그대로

            // (C) 매핑 저장
            var link = UserAccountProvider.builder()
                    .user(user)
                    .provider(naver)
                    .providerUserId(providerUserId)
                    .email(providerEmail)
                    .build();
            uapRepository.save(link);
            log.info("[네이버] 계정 로그인 성공 — 신규 연동을 생성했습니다. -> userId={}, providerUserId={}", user.getId(), n.getId());
        }

        return toLoginResponse(user, ProviderType.NAVER);
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

        KakaoMeResponse k = Optional.ofNullable(resp.getBody())
                .orElseThrow(() -> new RuntimeException("Kakao response empty"));

        String kakaoId = String.valueOf(k.getId());
        String providerEmail = (k.getKakaoAccount() != null) ? k.getKakaoAccount().getEmail() : null;
        String nickName = (k.getKakaoAccount() != null && k.getKakaoAccount().getProfile() != null)
                ? k.getKakaoAccount().getProfile().getNickname()
                : "카카오사용자";

        Provider kakao = providerRepository.findByProviderType(ProviderType.KAKAO)
                .orElseThrow(() -> new IllegalStateException("Provider Kakao not seeded"));

        // (A) (provider, providerId) 매핑 존재 여부 확인
        var linkOpt = uapRepository.findLink(ProviderType.KAKAO, kakaoId);

        User user;

        if (linkOpt.isPresent()) {
            // 이미 연결된 유저면 그 유저로 로그인
            user = linkOpt.get().getUser();
            log.info("[카카오] 계정 로그인 성공 — 기존 연동을 재사용합니다. -> userId={}, providerUserId={}", user.getId(), k.getId());
        } else {
            // (B) 없으면 기존 로직으로 유저 생성
            user = findOrCreateUser(nickName, providerEmail); // 네가 쓰던 생성 로직 그대로

            // (C) 매핑 저장
            var link = UserAccountProvider.builder()
                    .user(user)
                    .provider(kakao)
                    .providerUserId(kakaoId)
                    .email(providerEmail)
                    .build();
            uapRepository.save(link);
            log.info("[카카오] 계정 로그인 성공 — 신규 연동을 생성했습니다. -> userId={}, providerUserId={}", user.getId(), k.getId());
        }

        return toLoginResponse(user, ProviderType.KAKAO);
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
        GoogleUserInfo g = Optional.ofNullable(resp.getBody())
                .orElseThrow(() -> new RuntimeException("Google userinfo empty"));

        String googleId = g.getSub();
        String providerEmail = g.getEmail();    // null 가능
        String nickName = (g.getName() != null && !g.getName().isBlank()) ? g.getName() : "Google사용자";

        Provider google = providerRepository.findByProviderType(ProviderType.GOOGLE)
                .orElseThrow(() -> new IllegalStateException("Provider GOOGLE not seeded"));


        // (A) (provider, providerId) 매핑 존재 여부 확인
        var linkOpt = uapRepository.findLink(ProviderType.GOOGLE, googleId);

        User user;

        if (linkOpt.isPresent()) {
            // 이미 연결된 유저면 그 유저로 로그인
            user = linkOpt.get().getUser();
            log.info("[구글] 계정 로그인 성공 — 기존 연동을 재사용합니다. -> userId={}, providerUserId={}", user.getId(), googleId);
        } else {
            // (B) 없으면 기존 로직으로 유저 생성
            user = findOrCreateUser(nickName, providerEmail); // 네가 쓰던 생성 로직 그대로

            // (C) 매핑 저장
            var link = UserAccountProvider.builder()
                    .user(user)
                    .provider(google)
                    .providerUserId(googleId)
                    .email(providerEmail)
                    .build();
            uapRepository.save(link);
            log.info("[구글] 계정 로그인 성공 — 신규 연동을 생성했습니다. -> userId={}, providerUserId={}", user.getId(), googleId);
        }

        return toLoginResponse(user, ProviderType.GOOGLE);
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
                .username(username)
                .password(BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()))
                .email(email)        // null 허용
                .role(UserRole.USER)
                .level(UserLevel.BEGINNER)
                .score(0)
                .build();
    }

    private SocialLoginResponse.LoginDTO toLoginResponse(User user, ProviderType currentProvider) {
        String jwt = JwtUtil.create(user);

        // "Bearer "가 이미 붙어있으면 중복 방지
        String accessToken = (jwt != null && jwt.startsWith("Bearer "))
                ? jwt
                : "Bearer " + jwt;

        // linked 구성
        List<SocialLoginResponse.LinkedAccountDTO> linked =
                uapRepository.findAllByUserId(user.getId()).stream()
                        .map(uap -> new SocialLoginResponse.LinkedAccountDTO(
                                uap.getProvider().getProviderType().name().toLowerCase(Locale.ROOT),
                                Optional.ofNullable(uap.getEmail()).orElse("")
                        ))
                        .toList();

        return SocialLoginResponse.LoginDTO.builder()
                .accessToken(accessToken)
                .user(SocialLoginResponse.UserDTO.of(user, false))
                .linked(linked) //
                .build();
    }


    /**
     * 이메일 검증 로직입니다. 이메일 전송 . 이메일 코드 레디스에 저장
     *
     * @param email 유저가 입력한 email
     */
    public UserResponse.SendEmailDTO sendVerificationEmail(String email) {

        // 1. 중복 체크
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new Exception400("이미 존재하는 이메일입니다.");
        }

        // 2. 인증번호 생성 + Redis 저장
        String authCode = EmailCode.generateAuthCode();
        redisTemplate.opsForValue().set("email_auth:" + email, authCode, Duration.ofMinutes(5));

        // 3. 이메일 발송
        String url = "https://api.resend.com/emails";
        Map<String, Object> payload = new HashMap<>();
        payload.put("from", "짜바 <onboarding@resend.dev>");
        payload.put("to", email);
        payload.put("subject", "이메일 인증번호 안내");
        payload.put("html", "<p>당신의 인증번호는 <strong>" + authCode + "</strong> 입니다.</p>");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return new UserResponse.SendEmailDTO(email, false);
        }

        return new UserResponse.SendEmailDTO(email, true);
    }


    /**
     * 닉네임 중복체크 로직입니다.
     *
     * @param nickname 유저가 입력한 닉네임
     */
    public UserResponse.CheckNicknameDTO isNicknameAvailable(String nickname) {
        return new UserResponse.CheckNicknameDTO(userRepository.findByUsername(nickname).isEmpty());
    }


    // 회원가입
    @Transactional
    public UserResponse.JoinDTO createUser(UserRequest.JoinDTO reqDTO) {
        // 이메일 중복 체크
        userRepository.findByEmail(reqDTO.getEmail()).ifPresent(user -> {
            throw new Exception400("이미 존재하는 이메일입니다.");
        });

        // 비밀번호를 암호화해서 저장
        String encodedPassword = passwordEncoder.encode(reqDTO.getPassword());

        // 유저 객체 생성
        User user = reqDTO.toEntity(encodedPassword);

        // 유저 객체 저장
        userRepository.save(user);

        //  회원가입 로그
        log.info("{}({})이 회원가입 하였습니다.", user.getUsername(), user.getId());

        // JWT 토큰 생성
        String jwtToken = JwtUtil.create(user);

        return new UserResponse.JoinDTO(user, jwtToken);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        return new PrincipalDetails(user);
    }

    public UserResponse.LoginDTO emailLogin(UserRequest.LoginDTO reqDTO) {
        // 1. 사용자 존재 여부 확인
        User userOP = userRepository.findByEmail(reqDTO.getEmail())
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // 2. 비밀번호 일치 확인
        if (!passwordEncoder.matches(reqDTO.getPassword(), userOP.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String jwtToken = JwtUtil.create(userOP);

        // 4. 응답 DTO 반환 (로그인 DTO 필요)
        return new UserResponse.LoginDTO(userOP, jwtToken);
    }

    public UserResponse.VerifyEmailRespDTO verifyEmailCode(String email, String code) {
        String key = "email_auth:" + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            return new UserResponse.VerifyEmailRespDTO(email, false, "인증번호가 만료되었거나 존재하지 않습니다.");
        }

        if (!savedCode.equals(code)) {
            return new UserResponse.VerifyEmailRespDTO(email, false, "인증번호가 일치하지 않습니다.");
        }

        // 성공 시 Redis에서 삭제 (1회용)
        redisTemplate.delete(key);

        return new UserResponse.VerifyEmailRespDTO(email, true, "인증이 완료되었습니다.");
    }
}
