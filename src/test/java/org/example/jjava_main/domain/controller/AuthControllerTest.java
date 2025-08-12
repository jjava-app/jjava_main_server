package org.example.jjava_main.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main.controller.WorkspaceController;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.domain.user.admin.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(WorkspaceController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private AdminService adminService; // 컨트롤러가 주입받는 서비스만 Mock

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1)
                .email("ssar@naver.com")
                .username("ssar")
                .level(UserLevel.EXPERT)
                .role(UserRole.ADMIN)
                .build();
    }

    @Test
    @DisplayName("이메일 인증")
    void get_challenges_test() throws Exception {
        // given
        String email = "ssar@google.com";

        String requestBody = om.writeValueAsString(email);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/auth/email/check/{email}", email)
        );


        // eye
//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        log.debug("✅응답 바디: " + responseBody);

        // then
//        actions.andExpect(jsonPath("$.status").value(200));
//        actions.andExpect(jsonPath("$.msg").value("성공"));
//
//
//        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}
