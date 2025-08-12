package org.example.jjava_main.domain.controller;

import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository; // EntityManager 기반 레포지토리

    Authentication auth;

    private User seedUser;

    @BeforeEach
    void setUp() {
        // 더미에 맞는 이메일로 조회 (id 하드코딩 회피)
        seedUser = userRepository.findByEmail("cos1234@nate.com")
                .orElseThrow(() -> new IllegalStateException("seed user not found in data.sql"));
        auth = new UsernamePasswordAuthenticationToken(seedUser, null, seedUser.getAuthorities());
    }

    @Test
    @DisplayName("GET /users/mypage → rank 포함 & 응답 출력")
    void get_my_page_profile_success() throws Exception {
        ResultActions ra = mockMvc.perform(get("/users/mypage").with(authentication(auth)))
                .andDo(print()) // 요청/응답 전체 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(seedUser.getId()))
                .andExpect(jsonPath("$.body.email").value("cos1234@nate.com"))
                .andExpect(jsonPath("$.body.username").value("cos"))
                .andExpect(jsonPath("$.body.level").value("BEGINNER"))
                .andExpect(jsonPath("$.body.score").value(95))
                .andExpect(jsonPath("$.body.rank").value(2));

        String json = ra.andReturn().getResponse().getContentAsString();
        System.out.println("🔍 MyPage JSON => " + json); // 추가 출력
    }

    @Test
    @DisplayName("PUT /users/mypage/level → rank 제외 & 응답 출력")
    void update_user_level_success() throws Exception {
        String req = """
                    {"level":"EXPERT","username":"cos-up"}
                """;

        ResultActions ra = mockMvc.perform(
                        put("/users/mypage/level")
                                .with(authentication(auth))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(req)
                )
                .andDo(print()) // 요청/응답 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.username").value("cos-up"))
                .andExpect(jsonPath("$.body.level").value("EXPERT"));

        String json = ra.andReturn().getResponse().getContentAsString();
        System.out.println("🔁 LevelUpdate JSON => " + json);
    }
}