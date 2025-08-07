package org.example.jjava_main.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({UserControllerTest.TestConfig.class, UserControllerTest.TestSecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User mockUser;

    // ✅ 테스트용 UserService Bean 등록
    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    // ✅ 테스트용 SecurityFilterChain 등록 (모든 요청 허용)
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUpSecurityContext() {
        // ✅ mock 유저 생성
        mockUser = User.builder()
                .id(1)
                .email("test@example.com")
                .username("짜바")
                .level(UserLevel.EXPERT)
                .role(UserRole.USER)
                .build();

        // ✅ 인증 객체 수동 등록
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void 마이페이지_프로필_조회_성공() throws Exception {
        // given
        UserResponse response = new UserResponse(mockUser);
        when(userService.getUser(any(User.class))).thenReturn(response);

        // when
        MvcResult result = mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(1))
                .andExpect(jsonPath("$.body.email").value("test@example.com"))
                .andExpect(jsonPath("$.body.level").value("EXPERT"))
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("🔍 마이페이지 응답 JSON: " + responseBody);
    }

    @Test
    void 유저_학습난이도_변경_성공() throws Exception {
        // given
        UserRequest.UpdateLevelDTO reqDTO = new UserRequest.UpdateLevelDTO(UserLevel.EXPERT);

        User updatedUser = User.builder()
                .id(1)
                .email("test@example.com")
                .username("짜바")
                .level(UserLevel.BEGINNER)
                .role(UserRole.USER)
                .build();

        UserResponse respDTO = new UserResponse(updatedUser);
        when(userService.updateLevel(any(UserRequest.UpdateLevelDTO.class), any(User.class)))
                .thenReturn(respDTO);

        // when & then
        MvcResult result = mockMvc.perform(put("/user/level")
                        .contentType("application/json")
                        .content("""
                                {
                                  "level": "BEGINNER"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.level").value("BEGINNER"))
                .andExpect(jsonPath("$.body.id").value(1))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("🔁 난이도 변경 응답 JSON: " + responseBody);


    }
}
