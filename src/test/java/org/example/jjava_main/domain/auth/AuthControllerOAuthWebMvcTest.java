package org.example.jjava_main.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main.controller.AuthController;
import org.example.jjava_main.dto.SocialLoginRequest;
import org.example.jjava_main.dto.SocialLoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerOAuthWebMvcTest extends MyRestDoc {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("네이버 소셜 로그인 - 성공")
    void naver_oauth_login_ok() throws Exception {
        // given
        var req = new SocialLoginRequest.LoginDTO("dummy-token");
        String requestBody = om.writeValueAsString(req);
        System.out.println("[REQ] POST /login/naver " + requestBody);

        var user = SocialLoginResponse.UserDTO.builder()
                .id(1)
                .email("ssar@naver.com")
                .username("ssar")
                .role("USER")
                .isNewUser(false)
                .build();

        var resp = SocialLoginResponse.LoginDTO.builder()
                .accessToken("server-jwt-token-naver")
                .user(user)
                .linked(List.of(
                        new SocialLoginResponse.LinkedAccountDTO("naver", "ssar@naver.com")
                ))
                .build();

        Mockito.when(authService.naverOauthLogin(eq("dummy-token"))).thenReturn(resp);

        // when
        ResultActions actions = mvc.perform(
                post("/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // eye (응답 출력)
        int status = actions.andReturn().getResponse().getStatus();
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("[RES] status=" + status + " body=" + StringUtils.abbreviate(responseBody, 1500));

        // then
        actions.andExpect(status().isOk())
                // Resp.ok(...) 래퍼 가정: $.status / $.msg / $.body...
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.accessToken").value("server-jwt-token-naver"))
                .andExpect(jsonPath("$.body.user.id").value(1))
                .andExpect(jsonPath("$.body.user.email").value("ssar@naver.com"))
                .andExpect(jsonPath("$.body.user.username").value("ssar"))
                .andExpect(jsonPath("$.body.user.role").value("USER"))
                .andExpect(jsonPath("$.body.user.isNewUser").value(false))
                .andExpect(jsonPath("$.body.linked[0].provider").value("naver"))
                .andExpect(jsonPath("$.body.linked[0].email").value("ssar@naver.com"))
                .andDo(document);
    }

    @Test
    @DisplayName("카카오 소셜 로그인 - 성공")
    void kakao_oauth_login_ok() throws Exception {
        // given
        var req = new SocialLoginRequest.LoginDTO("dummy-token");
        String requestBody = om.writeValueAsString(req);
        System.out.println("[REQ] POST /login/kakao " + requestBody);

        var user = SocialLoginResponse.UserDTO.builder()
                .id(2)
                .email("cos@kakao.com")
                .username("cos")
                .role("USER")
                .isNewUser(false)
                .build();

        var resp = SocialLoginResponse.LoginDTO.builder()
                .accessToken("server-jwt-token-kakao")
                .user(user)
                .linked(List.of(
                        new SocialLoginResponse.LinkedAccountDTO("kakao", "cos@kakao.com")
                ))
                .build();

        Mockito.when(authService.kakaoOauthLogin(eq("dummy-token"))).thenReturn(resp);

        // when
        ResultActions actions = mvc.perform(
                post("/login/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // eye
        int status = actions.andReturn().getResponse().getStatus();
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("[RES] status=" + status + " body=" + StringUtils.abbreviate(responseBody, 1500));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.accessToken").value("server-jwt-token-kakao"))
                .andExpect(jsonPath("$.body.user.id").value(2))
                .andExpect(jsonPath("$.body.user.email").value("cos@kakao.com"))
                .andExpect(jsonPath("$.body.user.username").value("cos"))
                .andExpect(jsonPath("$.body.user.role").value("USER"))
                .andExpect(jsonPath("$.body.user.isNewUser").value(false))
                .andExpect(jsonPath("$.body.linked[0].provider").value("kakao"))
                .andExpect(jsonPath("$.body.linked[0].email").value("cos@kakao.com"))
                .andDo(document);
    }

    @Test
    @DisplayName("구글 소셜 로그인 - 성공")
    void google_oauth_login_ok() throws Exception {
        // given
        var req = new SocialLoginRequest.LoginDTO("dummy-token");
        String requestBody = om.writeValueAsString(req);
        System.out.println("[REQ] POST /login/google " + requestBody);

        var user = SocialLoginResponse.UserDTO.builder()
                .id(3)
                .email("love@gmail.com")
                .username("love")
                .role("USER")
                .isNewUser(false)
                .build();

        var resp = SocialLoginResponse.LoginDTO.builder()
                .accessToken("server-jwt-token-google")
                .user(user)
                .linked(List.of(
                        new SocialLoginResponse.LinkedAccountDTO("google", "love@gmail.com")
                ))
                .build();

        Mockito.when(authService.googleOauthLogin(eq("dummy-token"))).thenReturn(resp);

        // when
        ResultActions actions = mvc.perform(
                post("/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // eye
        int status = actions.andReturn().getResponse().getStatus();
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("[RES] status=" + status + " body=" + StringUtils.abbreviate(responseBody, 1500));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.accessToken").value("server-jwt-token-google"))
                .andExpect(jsonPath("$.body.user.id").value(3))
                .andExpect(jsonPath("$.body.user.email").value("love@gmail.com"))
                .andExpect(jsonPath("$.body.user.username").value("love"))
                .andExpect(jsonPath("$.body.user.role").value("USER"))
                .andExpect(jsonPath("$.body.user.isNewUser").value(false))
                .andExpect(jsonPath("$.body.linked[0].provider").value("google"))
                .andExpect(jsonPath("$.body.linked[0].email").value("love@gmail.com"))
                .andDo(document);
    }

}
