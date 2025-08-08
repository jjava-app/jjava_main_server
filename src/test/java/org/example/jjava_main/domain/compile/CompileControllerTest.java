package org.example.jjava_main.domain.compile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CompileControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @MockBean
    private HttpUtil httpUtil; // 실제 통신 대신 Mock


    @Test
    public void compile_proxy_success_test() throws Exception {
        // given
        Integer userId = 2;
        String type = "javascript";
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

        CompileRequest.DTO reqDTO = new CompileRequest.DTO(type, payload);

        // 가짜 응답 세팅
        CompileResponse.DTO mockResp = new CompileResponse.DTO(userId, payload, "B 학점");
        Mockito.when(httpUtil.compileServerSend(Mockito.any(), Mockito.any()))
                .thenReturn(mockResp);

        String requestBody = om.writeValueAsString(reqDTO);

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
        Integer userId = 2;
        String type = "javascript";
        String payload = "window.alert(x); let x = 10;";

        CompileRequest.DTO reqDTO = new CompileRequest.DTO(type, payload);

        // 가짜 응답 세팅
        CompileResponse.DTO mockResp = new CompileResponse.DTO(userId, null, "정의되지 않은 변수를 사용하고 있습니다.");
        Mockito.when(httpUtil.compileServerSend(Mockito.any(), Mockito.any()))
                .thenReturn(mockResp);
s
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(userId));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.result").value("정의되지 않은 변수를 사용하고 있습니다."));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}
