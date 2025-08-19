package org.example.jjava_main.domain.leaderboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.controller.LeaderboardController;
import org.example.jjava_main.domain.user.*;
import org.example.jjava_main.dto.LeaderboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LeaderboardController.class)
@AutoConfigureRestDocs // build/generated-snippets/ 아래에 스니펫 생성
@Import({LeaderboardControllerTest.TestConfig.class, LeaderboardControllerTest.TestSecurityConfig.class})
public class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private LeaderboardService leaderboardService;

    private User mockUser;

    // --- 테스트용 의존성 주입 ---
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    // --- 시큐리티 최소화(permitAll) ---
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void set_up_security_context() {
        mockUser = User.builder()
                .id(1)
                .email("ssar@naver.com")
                .username("ssar")
                .level(UserLevel.EXPERT)
                .role(UserRole.USER)
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void top10_test() throws Exception {
        // given: 서비스 응답 목킹 (currentScore + delta + rank)
        var items = List.of(
                LeaderboardResponse.ItemDTO.builder().userId(1).username("ssar").currentScore(1230).delta(230).rank(1).build(),
                LeaderboardResponse.ItemDTO.builder().userId(2).username("cos").currentScore(1180).delta(180).rank(2).build(),
                LeaderboardResponse.ItemDTO.builder().userId(3).username("love").currentScore(1100).delta(100).rank(3).build(),
                LeaderboardResponse.ItemDTO.builder().userId(4).username("haha").currentScore(1000).delta(90).rank(4).build()
        );
        var dto = LeaderboardResponse.DTO.builder().rankingList(items).build();

        when(leaderboardService.top10List()).thenReturn(dto);

        // when + then
        mockMvc.perform(get("/leaderboard/top10").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.rankingList", hasSize(4)))

                .andExpect(jsonPath("$.body.rankingList[0].userId").value(1))
                .andExpect(jsonPath("$.body.rankingList[0].username").value("ssar"))
                .andExpect(jsonPath("$.body.rankingList[0].currentScore").value(1230))
                .andExpect(jsonPath("$.body.rankingList[0].delta").value(230))
                .andExpect(jsonPath("$.body.rankingList[0].rank").value(1))

                .andDo(document("leaderboard/top10_test",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                fieldWithPath("body.rankingList[].userId").description("사용자 ID"),
                                fieldWithPath("body.rankingList[].username").description("사용자 닉네임"),
                                fieldWithPath("body.rankingList[].currentScore").description("현재 점수(after_score)"),
                                fieldWithPath("body.rankingList[].delta").description("증가분(오늘 상승량, Δ = after - before)"),
                                fieldWithPath("body.rankingList[].rank").description("순위 (DENSE_RANK)")
                        )
                ));
    }
}
