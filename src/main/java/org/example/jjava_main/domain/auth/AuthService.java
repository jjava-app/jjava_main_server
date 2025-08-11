package org.example.jjava_main.domain.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.error.ex.Exception400;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main._core.util.EmailCode;
import org.example.jjava_main._core.util.JwtUtil;
import org.example.jjava_main._core.util.PrincipalDetails;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${resend.api-key}")
    private String apiKey;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 검증 로직입니다.
     *
     * @param email 유저가 입력한 email
     */
    public UserResponse.CheckEmailDTO verificationEmail(String email) {

        // 존재하는 이메일인지 체크
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            throw new Exception400("이미 존재하는 이메일입니다.");
        }

        String authCode = EmailCode.generateAuthCode(); // 인증번호 생성
        String url = "https://api.resend.com/emails"; // 이메일 보내주는 api 주소

        // 요청 바디 데이터
        Map<String, Object> payload = new HashMap<>();
        payload.put("from", "짜바 <onboarding@resend.dev>");
        payload.put("to", email);
        payload.put("subject", "이메일 인증번호 안내");
        payload.put("html", "<p>당신의 인증번호는 <strong>" + authCode + "</strong> 입니다.</p>");

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // HttpEntity 생성 (헤더 + 바디)
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        // 전송 실패 시 throw
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("이메일 전송 실패: " + response.getBody());
        }
        return new UserResponse.CheckEmailDTO(response.getStatusCode().is2xxSuccessful());
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

    public UserResponse.LoginDTO login(UserRequest.LoginDTO reqDTO) {
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
}
