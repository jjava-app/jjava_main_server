package org.example.jjava_main.domain.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class HomeControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private User mockUser;

    @BeforeEach
    void set_up_security_context() {
        mockUser = User.builder()
                .id(2)
                .email("cos1234@naver.com")
                .username("cos")
                .level(UserLevel.INTERMEDIATE)
                .role(UserRole.USER)
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void home_test() throws Exception {

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/home")
        );


        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.body.userDTO.id").value(2));
        actions.andExpect(jsonPath("$.body.userDTO.email").value("cos1234@naver.com"));
        actions.andExpect(jsonPath("$.body.userDTO.username").value("cos"));
        actions.andExpect(jsonPath("$.body.userDTO.level").value("INTERMEDIATE"));
        actions.andExpect(jsonPath("$.body.userDTO.score").value(0));
        actions.andExpect(jsonPath("$.body.userDTO.rank").value(1));

        actions.andExpect(jsonPath("$.body.leaderboardDTO.rankingList[0].userId").value(2));
        actions.andExpect(jsonPath("$.body.leaderboardDTO.rankingList[0].username").value("ssar"));
        actions.andExpect(jsonPath("$.body.leaderboardDTO.rankingList[0].currentScore").value(120));
        actions.andExpect(jsonPath("$.body.leaderboardDTO.rankingList[0].delta").value(120));
        actions.andExpect(jsonPath("$.body.leaderboardDTO.rankingList[0].rank").value(1));

        actions.andExpect(jsonPath("$.body.sqDTO.sqList[0].solvedQuestionId").value(1));
        actions.andExpect(jsonPath("$.body.sqDTO.sqList[0].questionId").value(5));
        actions.andExpect(jsonPath("$.body.sqDTO.sqList[0].title").value("두 수의 몫 구하기"));
        actions.andExpect(jsonPath("$.body.sqDTO.sqList[0].questionType").value("OPERATOR"));



        actions.andDo(print());
        actions.andDo(document);

    }
}
