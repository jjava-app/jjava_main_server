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

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public Object naverOauthLogin(String accessToken, String fcmToken) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<NaverMeResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, request, NaverMeResponse.class
        );
        var u = resp.getBody().response(); // id, email, name, nickname, profile_image...

        // todo: 테스트한다고 빨리 만들었는데 이메일 중복체크 같은 로직 추가 및 나중에 컨벤션 맞게 수정해서 만들기
        String uname = "NAVER_" + u.id().substring(0, 8) + "_" + (System.currentTimeMillis() % 10000);
        String displayName = (u.name() != null && !u.name().isBlank()) ? u.name()
                : (u.nickname() != null ? u.nickname() : "네이버사용자");

        User user = User.builder()
                .username(uname)
                .password(BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt()))
                .username(displayName)
                .email(u.email())
                .role(UserRole.USER)
//                .providerType(ProviderType.NAVER) 지금은 네이버만 할꺼라 provider enum 추가안했음
                .build();

        userRepository.save(user); // ← 저장만
        String myAccessToken = JwtUtil.create(user);
        return SocialLoginResponse.LoginDTO.builder()
                .accessToken(myAccessToken)
                .user(SocialLoginResponse.UserDTO.of(user))
                .build();

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

}
