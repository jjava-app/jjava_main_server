package org.example.jjava_main.domain.compile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main.controller.CheckController;
import org.example.jjava_main.domain.question.ProgressStatus;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.example.jjava_main.domain.question.QuestionType;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
import org.example.jjava_main.dto.QuestionRequest;
import org.example.jjava_main.dto.QuestionResponse;
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

import java.util.List;
import java.util.Map;

@WebMvcTest(controllers = CheckController.class)
@Import({CheckControllerTest.TestSecurityConfig.class})
public class CheckControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @MockBean // 스프링이 관리하는 빈을 Mockito Mock 객체로 교체해주는 어노테이션
    private CheckService checkService;

    @MockBean  // 스프링이 관리하는 빈을 Mockito Mock 객체로 교체해주는 어노테이션
    private HttpUtil httpUtil;

    @MockBean
    private QuestionRepository questionRepository;

    private Question mockQuestion;
    private User mockUser;
    private SolvedQuestion mockSolvedQuestion;

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

        mockQuestion = Question.builder()
                .type(QuestionType.TEXT)
                .title("title")
                .content("content")
                .testVariable("[{\"a\":\"Hello\",\"b\":\"World\"}]")
                .testAnswer("[\"HelloWorld\"]")
                .build();

        mockSolvedQuestion = SolvedQuestion.builder()
                .question(mockQuestion)
                .user(mockUser)
                .AiComment("comment")
                .serializedJson("serializedJson")
                .blockExtensionJson("blockExtensionJson")
                .build();
    }


    @Test
    public void check_proxy_and_code_refactor_success_test() throws Exception {
        // given
        String type = "javascript";
        String payload = "function repeatHello(a) { return a.repeat(5); }";


        // 테스트 케이스
        CheckRequest.DTO.TestSpecDTO t1 = new CheckRequest.DTO.TestSpecDTO(Map.of("a", "Hello"), ("HelloHelloHelloHelloHello"));

        CheckRequest.DTO.TestSpecDTO t2 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "string")), ("stringstringstringstringstring"));

        CheckRequest.DTO.TestSpecDTO t3 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "int")), ("intintintintint"));

        CheckRequest.DTO reqDTO = new CheckRequest.DTO(type, payload, List.of(t1, t2, t3));


        // 컴파일 서버 결과(Proxy)를 Pass로 가짜 세팅
        CheckResponse.PassDTO passFromCompile = new CheckResponse.PassDTO();
        passFromCompile.setUserId(1);
        passFromCompile.setQuestionId(1);
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
                Mockito.eq(payload),
                Mockito.eq(1),
                Mockito.eq(1),
                Mockito.nullable(String.class),
                Mockito.nullable(String.class)
        )).thenReturn(aiRefactor);


        String requestBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/check")
                        .param("questionId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.passed").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.refactoredCode").value("// refactored code here"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.refactorNote").value("변수 이름 정리 및 반복 로직 단순화"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }


    @Test
    public void check_proxy_and_code_refactor_fail_test() throws Exception {
        // given
        String type = "javascript";
        String payload = "function repeatHello(a) { return a.repeat(3); }";


        // 테스트 케이스
        CheckRequest.DTO.TestSpecDTO t1 = new CheckRequest.DTO.TestSpecDTO(Map.of("a", "Hello"), ("HelloHelloHelloHelloHello"));

        CheckRequest.DTO.TestSpecDTO t2 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "string")), ("stringstringstringstringstring"));

        CheckRequest.DTO.TestSpecDTO t3 = new CheckRequest.DTO.TestSpecDTO((Map.of("a", "int")), ("intintintintint"));

        CheckRequest.DTO reqDTO = new CheckRequest.DTO(type, payload, List.of(t1, t2, t3));


        // 실패 응답 DTO (Proxy 서버)
        CheckResponse.FailDTO failFromCompile = new CheckResponse.FailDTO();
        failFromCompile.setUserId(1);
        failFromCompile.setQuestionId(1);
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
                        .param("questionId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.passed").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.index").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.failedInputs.a").value("string"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.message").value("오답입니다. 로직을 다시 확인하세요."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.expected").value("stringstringstringstringstring"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.result").value("stringstringstring"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);
    }

    @Test
    public void question_list_get_test() throws Exception {

        QuestionResponse.ListDTO respDTO = new QuestionResponse.ListDTO(
                1,
                5, // totalCount
                3, // solvedCount
                List.of(
                        new QuestionResponse.ListDTO.QuestionDTO(1, "텍스트", "dummy"),
                        new QuestionResponse.ListDTO.QuestionDTO(2, "텍스트", "dummy")
                ),
                List.of(
                        Question.builder().id(1).type(QuestionType.TEXT).title("테스트1").build(),
                        Question.builder().id(2).type(QuestionType.TEXT).title("테스트2").build(),
                        Question.builder().id(3).type(QuestionType.TEXT).title("테스트3").build()
                )
        );

        //  Stub 설정
        Mockito.when(checkService.questionListGet(1))
                .thenReturn(respDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/questions")
                        .param("userId", String.valueOf(1))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.solvedCount").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questions").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.solvedQuestionIds").isArray())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

    }


    @Test
    public void question_detail_get_test() throws Exception {
        QuestionResponse.DetailDTO respDTO = new QuestionResponse.DetailDTO(
                1,
                "title",
                "content"
        );

        //  Stub 설정
        Mockito.when(checkService.questionDetailGet(1))
                .thenReturn(respDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/questions/" + 1)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("content"))
                .andDo(document);
    }


    @Test
    public void solved_question_upsert_test() throws Exception {
        // given
        QuestionRequest.SolvedQuestionCreateDTO reqDTO = new QuestionRequest.SolvedQuestionCreateDTO(
                1,
                1,
                "json~~~`",
                "json~~~~"
        );


        QuestionResponse.SolvedQuestionCreateDTO respDTO = new QuestionResponse.SolvedQuestionCreateDTO(
                1,
                1,
                1,
                "json~~~~",
                "json~~~~",
                ProgressStatus.IN_PROGRESS
        );

        //  Stub 설정
        Mockito.when(checkService.solvedQuestionUpsert(
                reqDTO.getUserId(),
                reqDTO.getQuestionId(),
                reqDTO.getSerializedJson(),
                reqDTO.getBlockExtensionJson()
        )).thenReturn(respDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/solved-questions/{questionId}/{userId}", reqDTO.getQuestionId(), reqDTO.getUserId())

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reqDTO)) // ← DTO → JSON 변환
                        .accept(MediaType.APPLICATION_JSON)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.solvedQuestionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.serializedJson").value("json~~~~"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.blockExtensionJson").value("json~~~~"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.status").value("IN_PROGRESS"))
                .andDo(document);
    }

    @Test
    public void solved_question_detail_test() throws Exception {
        // given
        Integer questionId = 1;

        Question question = new Question().builder()
                .id(questionId)
                .title("문제 제목")
                .content("문제 내용")
                .build();

        SolvedQuestion solvedQuestion = new SolvedQuestion().builder()
                .id(questionId)
                .AiComment("AI 코멘트")
                .serializedJson("json~~~")
                .blockExtensionJson("json~~~")
                .build();

        QuestionResponse.SolvedQuestionDetailDTO respDTO = new QuestionResponse.SolvedQuestionDetailDTO(
                question, solvedQuestion
        );

        // Stub 설정
        Mockito.when(checkService.solvedQuestionDetailGet(questionId))
                .thenReturn(respDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/solved-questions/{id}", questionId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.title").value("문제 제목"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("문제 내용"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.aiComment").value("AI 코멘트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.serializedJson").value("json~~~"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.blockExtensionJson").value("json~~~"))
                .andDo(document); // RestDocs 문서화

    }
}
