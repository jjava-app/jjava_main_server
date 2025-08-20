package org.example.jjava_main.domain.user;

import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main.controller.UserController;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.example.jjava_main.dto.UserResponse.LevelUpdateResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({UserControllerTest.TestConfig.class, UserControllerTest.TestSecurityConfig.class})
class UserControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User mockUser;

    // 테스트용 UserService Bean 등록
    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    // 테스트용 SecurityFilterChain 등록 (모든 요청 허용)
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
    void set_up_security_context() {
        // mock 유저 생성
        mockUser = User.builder()
                .id(1)
                .email("ssar1234@nate.com")
                .username("ssar")
                .level(UserLevel.EXPERT)
                .role(UserRole.USER)
                .score(2530)
                .build();

        // 인증 객체 수동 등록
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void get_my_page_profile_success() throws Exception {
        // given
        UserResponse.DTO response = new UserResponse.DTO(mockUser, 155);
        when(userService.userGet(any(User.class))).thenReturn(response);

        // when
        mockMvc.perform(get("/users/mypage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(1))
                .andExpect(jsonPath("$.body.email").value("ssar1234@nate.com"))
                .andExpect(jsonPath("$.body.username").value("ssar"))
                .andExpect(jsonPath("$.body.level").value(UserLevel.EXPERT.name()))
                .andExpect(jsonPath("$.body.score").value(2530))
                .andExpect(jsonPath("$.body.rank").value(155))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }

    @Test
    void update_user_level_success() throws Exception {
        // given
        String reqJson = """
                {
                  "level": "BEGINNER",
                  "username": "ssar"
                }
                """;

        User updatedUser = User.builder()
                .id(1)
                .email("ssar1234@nate.com")
                .username("ssar")
                .level(UserLevel.BEGINNER)
                .role(UserRole.USER)
                .score(2530)
                .build();

        LevelUpdateResponse respDTO = new LevelUpdateResponse(updatedUser);
        when(userService.levelUpdate(any(UserRequest.LevelUpdateDTO.class), any(User.class)))
                .thenReturn(respDTO);

        // when & then
        mockMvc.perform(put("/users/mypage/level")
                        .contentType("application/json")
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.level").value("BEGINNER"))
                .andExpect(jsonPath("$.body.username").value("ssar"))
                .andExpect(jsonPath("$.body.id").value(1))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }
}
