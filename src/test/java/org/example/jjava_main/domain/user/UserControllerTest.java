package org.example.jjava_main.domain.user;

import org.example.jjava_main.controller.UserController;
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
                .email("ssar@naver.com")
                .username("ssar")
                .level(UserLevel.EXPERT)
                .role(UserRole.USER)
                .build();

        // ✅ 인증 객체 수동 등록
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getMyPageProfile_success() throws Exception {
        // given
        UserResponse response = new UserResponse(mockUser);
        when(userService.userGet(any(User.class))).thenReturn(response);

        // when
        MvcResult result = mockMvc.perform(get("/users/mypage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(1))
                .andExpect(jsonPath("$.body.email").value("test@example.com"))
                .andExpect(jsonPath("$.body.level").value("EXPERT"))
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("🔍 MyPage response JSON: " + responseBody);
    }

    @Test
    void updateUserLevel_success() throws Exception {
        // given
        UserRequest.LevelUpdateDTO reqDTO = new UserRequest.LevelUpdateDTO(UserLevel.EXPERT);

        User updatedUser = User.builder()
                .id(1)
                .email("ssar@naver.com")
                .username("ssar")
                .level(UserLevel.BEGINNER)
                .role(UserRole.USER)
                .build();

        UserResponse respDTO = new UserResponse(updatedUser);
        when(userService.levelUpdate(any(UserRequest.LevelUpdateDTO.class), any(User.class)))
                .thenReturn(respDTO);

        // when & then
        MvcResult result = mockMvc.perform(put("/users/mypage/level")
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
        System.out.println("🔁 Update level response JSON: " + responseBody);


    }
}
