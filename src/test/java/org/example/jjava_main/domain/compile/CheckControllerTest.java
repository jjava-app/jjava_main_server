package org.example.jjava_main.domain.compile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.example.jjava_main.domain.question.QuestionType;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
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

import java.util.List;
import java.util.Map;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CheckControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @MockBean // 스프링이 관리하는 빈을 Mockito Mock 객체로 교체해주는 어노테이션
    private CheckService checkService;

    @Autowired
    private QuestionRepository questionRepository;

    @MockBean  // 스프링이 관리하는 빈을 Mockito Mock 객체로 교체해주는 어노테이션
    private HttpUtil httpUtil;


    @Test
    public void check_proxy_and_code_refactor_success_test() throws Exception {
        // given
        Question q = new Question();
        q.setType(QuestionType.TEXT); // @Enumerated(EnumType.STRING) 권장
        q.setTitle("dummy");
        q.setContent("dummy");
        q.setTestVariable("[{\"a\":\"Hello\",\"b\":\"World\"}]");
        q.setTestAnswer("[\"HelloWorld\"]");
        questionRepository.save(q);
        Integer questionId = q.getId();

        Integer userId = 2;
        String type = "javascript";
        String payload = "function repeatHello(a) { return a.repeat(5); }";


        // 테스트 케이스
        CheckRequest.DTO.TestSpecDTO t1 = new CheckRequest.DTO.TestSpecDTO(Map.of("a", "Hello"), ("HelloHelloHelloHelloHello"));

        CheckRequest.DTO.TestSpecDTO t2 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "string")), ("stringstringstringstringstring"));

        CheckRequest.DTO.TestSpecDTO t3 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "int")), ("intintintintint"));

        CheckRequest.DTO reqDTO = new CheckRequest.DTO(type, payload, List.of(t1, t2, t3));


        // 컴파일 서버 결과(Proxy)를 Pass로 가짜 세팅
        CheckResponse.PassDTO passFromCompile = new CheckResponse.PassDTO();
        passFromCompile.setUserId(userId);
        passFromCompile.setQuestionId(questionId);
        passFromCompile.setPassed(true);
        passFromCompile.setCode(payload);

        // 통신 Mock 처리
        Mockito.when(httpUtil.checkServerSend(
                Mockito.any(CheckRequest.DTO.class),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(passFromCompile);

        // AI 리팩토링 결과도 가짜 세팅
        CheckResponse.PassDTO aiRefactor = new CheckResponse.PassDTO();
        aiRefactor.setRefactoredCode("// refactored code here");
        aiRefactor.setRefactorNote("변수 이름 정리 및 반복 로직 단순화");

        // ai 리팩토링 Mock 처리
        Mockito.when(checkService.checkAndCodeRefactor(
                Mockito.eq(payload), Mockito.eq(questionId), Mockito.eq(userId)
        )).thenReturn(aiRefactor);


        String requestBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/check")
                        .param("questionId", questionId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.passed").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(questionId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.refactoredCode").value("// refactored code here"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.refactorNote").value("변수 이름 정리 및 반복 로직 단순화"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }


    @Test
    public void check_proxy_and_code_refactor_fail_test() throws Exception {
        // given
        Question q = new Question();
        q.setType(QuestionType.TEXT); // @Enumerated(EnumType.STRING) 권장
        q.setTitle("dummy");
        q.setContent("dummy");
        q.setTestVariable("[{\"a\":\"Hello\",\"b\":\"World\"}]");
        q.setTestAnswer("[\"HelloWorld\"]");
        questionRepository.save(q);
        Integer questionId = q.getId();

        Integer userId = 2;
        String type = "javascript";
        String payload = "function repeatHello(a) { return a.repeat(3); }";


        // 테스트 케이스
        CheckRequest.DTO.TestSpecDTO t1 = new CheckRequest.DTO.TestSpecDTO(Map.of("a", "Hello"), ("HelloHelloHelloHelloHello"));

        CheckRequest.DTO.TestSpecDTO t2 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "string")), ("stringstringstringstringstring"));

        CheckRequest.DTO.TestSpecDTO t3 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "int")), ("intintintintint"));

        CheckRequest.DTO reqDTO = new CheckRequest.DTO(type, payload, List.of(t1, t2, t3));


        // 실패 응답 DTO (Proxy 서버)
        CheckResponse.FailDTO failFromCompile = new CheckResponse.FailDTO();
        failFromCompile.setUserId(userId);
        failFromCompile.setQuestionId(questionId);
        failFromCompile.setPassed(false);
        failFromCompile.setIndex(1); // 두 번째 케이스에서 실패했다고 가정
        failFromCompile.setFailedInputs(Map.of("a", "string"));
        failFromCompile.setMessage("오답입니다. 로직을 다시 확인하세요.");
        failFromCompile.setExpected("stringstringstringstringstring");
        failFromCompile.setResult("stringstringstring"); // 일부만 반복했다고 가정

        // 통신 Mock 처리 (실패 DTO 리턴)
        Mockito.when(httpUtil.checkServerSend(
                Mockito.any(CheckRequest.DTO.class),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(failFromCompile);

        String requestBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/check")
                        .param("questionId", questionId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.passed").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(questionId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.index").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.failedInputs.a").value("string"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.message").value("오답입니다. 로직을 다시 확인하세요."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.expected").value("stringstringstringstringstring"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.result").value("stringstringstring"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }
}
