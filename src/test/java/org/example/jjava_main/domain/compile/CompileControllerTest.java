package org.example.jjava_main.domain.compile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main.controller.CompileController;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(controllers = CompileController.class)
@Import({CompileControllerTest.TestSecurityConfig.class, QuestionRepository.class})
public class CompileControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @MockBean  // 스프링이 관리하는 빈을 Mockito Mock 객체로 교체해주는 어노테이션
    private HttpUtil httpUtil;

    @MockBean
    private QuestionRepository questionRepository;

    private User mockUser;

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
    void setUp() {
        mockUser = User.builder()
                .id(1)
                .email("ssar@naver.com")
                .username("ssar")
                .level(UserLevel.EXPERT)
                .role(UserRole.USER)
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    public void compile_proxy_success_test() throws Exception {
        // given
        String payload = """
                let score = 85;
                if (score >= 90) {
                  window.alert('A 학점');
                } else if (score >= 80) {
                  window.alert('B 학점');
                } else {
                  window.alert('재도전');
                }
                """;

        CompileRequest.DTO reqDTO = new CompileRequest.DTO(payload);

        // 가짜 응답 세팅
        CompileResponse.DTO mockResp = new CompileResponse.DTO(1, payload, "B 학점");

        // 통신 Mock 처리
        Mockito.when(httpUtil.compileServerSend(Mockito.any(), Mockito.any()))
                .thenReturn(mockResp);

        String requestBody = om.writeValueAsString(reqDTO);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/compile")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.result").value("B 학점"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }


    @Test
    public void compile_proxy_fail_test() throws Exception {
        // given
        String payload = "window.alert(x); let x = 10;";

        CompileRequest.DTO reqDTO = new CompileRequest.DTO(payload);

        // 가짜 응답 세팅
        CompileResponse.DTO mockResp = new CompileResponse.DTO(1, null, "정의되지 않은 변수를 사용하고 있습니다.");

        // 통신 Mock 처리
        Mockito.when(httpUtil.compileServerSend(Mockito.any(), Mockito.any()))
                .thenReturn(mockResp);

        String requestBody = om.writeValueAsString(reqDTO);


        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/compile")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)

        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.result").value("정의되지 않은 변수를 사용하고 있습니다."));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}
