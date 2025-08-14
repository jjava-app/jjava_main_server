package org.example.jjava_main.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main._core.util.JwtUtil;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(scripts = {"/db/clear.sql", "/db/data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    private String accessToken;

    @BeforeEach
    void setUp() {
        User ssar = User.builder()
                .id(1)
                .email("ssar@naver.com")
                .username("ssar")
                .level(UserLevel.EXPERT)
                .role(UserRole.ADMIN)
                .build();
        accessToken = JwtUtil.create(ssar);
    }

    @Test
    @DisplayName("닉네임 중복 체크 (중복 시)")
    void check_nickname_fail_test() throws Exception {
        // given
        String nickname = "ssar";

        String requestBody = om.writeValueAsString(nickname);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/auth/nickname/check/{nickname}", nickname)
        );


        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.available").value(false));


        actions.andDo(print()).andDo(document);
    }

    @Test
    @DisplayName("닉네임 중복 체크")
    void check_nickname_success_test() throws Exception {
        // given
        String nickname = "sssarr";

        String requestBody = om.writeValueAsString(nickname);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/auth/nickname/check/{nickname}", nickname)
        );


        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.available").value(true));


        actions.andDo(print()).andDo(document);
    }

    @Test
    @DisplayName("이메일 회원가입")
    void join_test() throws Exception {
        // given
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setNickname("ccos");
        reqDTO.setEmail("ccos@naver.com");
        reqDTO.setPassword("1234!!asdfWE");
        reqDTO.setLevel(UserLevel.EXPERT);

        String requestBody = om.writeValueAsString(reqDTO);
//        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/join")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );


        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.accessToken").isNotEmpty());
        actions.andExpect(jsonPath("$.body.email").value("ccos@naver.com"));
        actions.andExpect(jsonPath("$.body.nickname").value("ccos"));
        actions.andExpect(jsonPath("$.body.level").value("EXPERT"));
        actions.andExpect(jsonPath("$.body.role").value("USER"));


        actions.andDo(print()).andDo(document);
    }

    @Test
    @DisplayName("로그인 성공 - 세션과 응답 확인")
    void login_test() throws Exception {
        // given
        UserRequest.LoginDTO req = new UserRequest.LoginDTO();
        req.setEmail("ssar1234@nate.com");
        req.setPassword("1234");

        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/login")
                        .content(om.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        actions.andDo(print())
               .andExpect(status().isOk())
                // Resp.ok(...) 래핑 구조가 {"data": {...}} 라고 가정
                .andExpect(jsonPath("$.body.email").value("ssar1234@nate.com"))
                .andExpect(jsonPath("$.body.nickname").value("ssar"))
                // 토큰은 실제 값이 무엇이든 비어있지 않으면 OK (JwtUtil.create(...) 결과)
                .andExpect(jsonPath("$.body.accessToken").exists())
                .andDo(document);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 시 400")
    void login_wrong_password_test() throws Exception {
        // given
        UserRequest.LoginDTO req = new UserRequest.LoginDTO();
        req.setEmail("ssar1234@nate.com");
        req.setPassword("wrong-password");

        // when & then
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/login")
                        .content(om.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        actions.andDo(print())
               .andExpect(status().isBadRequest())
                // 에러 응답 바디 구조가 프로젝트마다 달라서, 메시지 텍스트로 판정 (필요 시 jsonPath로 조정)
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.msg").value("비밀번호가 일치하지 않습니다."))
                .andDo(document);
    }
}
