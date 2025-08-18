package org.example.jjava_main._core.config;

import org.example.jjava_main._core.error.Jwt401Handler;
import org.example.jjava_main._core.error.Jwt403Handler;
import org.example.jjava_main._core.filter.AuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 컨텍스트 홀더에 세션 저장할 때 사용하는 클래스
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. iframe 허용 : mysql로 전환 시 삭제
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // 2. csrf 허용 : HTML 사용 안 함
        http.csrf(csrf -> csrf.disable());

        // 3. 세션 비활성화 (STATELESS) : 세션 자체는 사용 가능하나, 정보를 계속 저장해두지는 않음
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4. 폼 로그인 비활성화 (JWT 사용하므로) : UsernamePasswordAuthenticationFilter 비활성화
        http.formLogin(form -> form.disable());

        // 5. HTTP Basic 인증 비활성화 : BasicAuthenticationFilter 비활성화
        // username, password를 직접 들고 다니기 때문에 제일 안전하나 유저가 이를 관리해야 함 : UX 저하
        http.httpBasic(basicLogin -> basicLogin.disable());

        // Q. 현재 인증 필터가 없는데 어떻게 인증을 수행할 건가?
        // 현재 이 예제에서는 서비스에서 인증 처리 진행 (login 직접 작성)
        // 또는 커스텀 필터를 작성해서 추가

        // 6. 커스텀 필터 작성
        // 인가 필터 장착 : 토큰이 정상적으로 인증되면 AwareFilter로 접근 허용
        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 7. 예외 처리 Handler 등록
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(new Jwt401Handler())
                .accessDeniedHandler(new Jwt403Handler()));

        /*http.formLogin(form -> form
                .loginPage("/login-form")
                .loginProcessingUrl("/login") // username=ssar&password=1234
                .defaultSuccessUrl("/main"));*/

        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers("/").authenticated()
                        .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/workspace/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/leaderboard/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/compile/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/check/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/questions/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/solved-questions/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
        );

        return http.build();
    }
}
