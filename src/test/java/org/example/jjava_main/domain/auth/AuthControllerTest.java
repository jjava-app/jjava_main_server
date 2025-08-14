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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
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


        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
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


        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
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


        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("네이버 소셜 로그인 - 성공")
    void naver_oauth_login_ok() throws Exception {
        // given


        // when


        // eye


        // then


    }
}

