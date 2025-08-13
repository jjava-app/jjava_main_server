package org.example.jjava_main.domain.user.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main.controller.AdminController;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.question.QuestionType;
import org.example.jjava_main.domain.user.*;
import org.example.jjava_main.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest extends MyRestDoc {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @MockBean private AdminService adminService;

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
    void user_list_test() throws Exception {
        var dto = new UserResponse.ListDTO(
                java.util.List.of(UserResponse.UserDTO.from(mockUser)),
                0,
                "",
                1,
                0
        );
        when(adminService.userList(0, "id", 0)).thenReturn(dto);

        mockMvc.perform(get("/admin/users").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.page").value(0))
                .andExpect(jsonPath("$.body.totalCount").value(1))
                .andExpect(jsonPath("$.body.sort").value(0))
                .andExpect(jsonPath("$.body.userList.length()").value(1))
                .andExpect(jsonPath("$.body.userList[0].id").value(1))
                .andExpect(jsonPath("$.body.userList[0].email").value("ssar@naver.com"))
                .andExpect(jsonPath("$.body.userList[0].username").value("ssar"))
                .andExpect(jsonPath("$.body.userList[0].level").value("EXPERT"))
                .andExpect(jsonPath("$.body.userList[0].role").value("ADMIN"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        verify(adminService, times(1)).userList(0, "id", 0);
    }

    @Test
    void user_list_with_params_test() throws Exception {
        when(adminService.userList(10, "email", 1))
                .thenReturn(new UserResponse.ListDTO(java.util.List.of(), 10, "email", 0, 1));

        mockMvc.perform(get("/admin/users")
                        .param("page", "10")
                        .param("order", "email")
                        .param("sort", "1")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.page").value(10))
                .andExpect(jsonPath("$.body.totalCount").value(0))
                .andExpect(jsonPath("$.body.sort").value(1))
                .andExpect(jsonPath("$.body.userList.length()").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        verify(adminService, times(1)).userList(10, "email", 1);
    }

    @Test
    void update_user_test() throws Exception {
        // given
        User userPS = User.builder()
                .id(1)
                .username("김쌀쌀")
                .email("ssarssar@nate.com")
                .role(UserRole.USER)
                .score(100)
                .build();

        var req = new UserRequest.UserUpdateDTO(
                "김쌀쌀",
                "ssarssar@nate.com",
                UserRole.USER,
                100
        );

        when(adminService.userUpdate(eq(1), any(UserRequest.UserUpdateDTO.class)))
                .thenReturn(new UserResponse.UserUpdateDTO(userPS));

        // when & then
        mockMvc.perform(
                        put("/admin/users/{id}", 1)
                                .contentType(APPLICATION_JSON)               // ✅ Content-Type 지정
                                .accept(APPLICATION_JSON)
                                .content(om.writeValueAsString(req))          // ✅ JSON 바디 전달
                )
                .andExpect(status().isOk())
                // 컨트롤러 응답이 공통 래핑 구조라면 $.body.* 로 검증
                .andExpect(jsonPath("$.body.id").value(1))
                .andExpect(jsonPath("$.body.username").value("김쌀쌀"))
                .andExpect(jsonPath("$.body.email").value("ssarssar@nate.com"))
                .andExpect(jsonPath("$.body.role").value("USER"))
                .andExpect(jsonPath("$.body.score").value(100))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        // verify
        verify(adminService, times(1)).userUpdate(eq(1), any(UserRequest.UserUpdateDTO.class));
    }

    @Test
    void delete_user_test() throws Exception {
        // given
        doNothing().when(adminService).userDelete(1);

        // when & then
        mockMvc.perform(
                        delete("/admin/users/{id}", 1)
                                .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                // Resp.ok(null) 구조가 {"body": null} 라면 아래 검증이 맞습니다.
                .andExpect(jsonPath("$.body", nullValue()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        // verify
        verify(adminService, times(1)).userDelete(1);
    }

    // =========================
    // 1) LIST
    // =========================
    @Test
    void question_list_default_test() throws Exception {
        // given: Question 엔티티 3건
        Question q1 = Question.builder()
                .id(1).type(QuestionType.OPERATOR)
                .title("두 수의 합 구하기")
                .content("문제 설명: ...")
                .testVariable("[{\"a\":2,\"b\":3}]")
                .testAnswer("[5]")
                .build();

        Question q2 = Question.builder()
                .id(2).type(QuestionType.OPERATOR)
                .title("두 수의 차 구하기")
                .content("문제 설명: ...")
                .testVariable("[{\"a\":5,\"b\":3}]")
                .testAnswer("[2]")
                .build();

        Question q3 = Question.builder()
                .id(3).type(QuestionType.OPERATOR)
                .title("두 수의 곱 구하기")
                .content("문제 설명: ...")
                .testVariable("[{\"a\":2,\"b\":3}]")
                .testAnswer("[6]")
                .build();

        var listDTO = new QuestionResponse.AdminListDTO(
                List.of(q1, q2, q3), // questions
                0,                   // page
                "id",                // order
                0,                   // sort
                3                    // totalCount
        );
        when(adminService.questionList(0, "id", 0)).thenReturn(listDTO);

        // when & then
        mockMvc.perform(get("/admin/questions").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.page").value(0))
                .andExpect(jsonPath("$.body.order").value("id"))
                .andExpect(jsonPath("$.body.sort").value(0))
                .andExpect(jsonPath("$.body.totalCount").value(3))
                .andExpect(jsonPath("$.body.questions.length()").value(3))
                .andExpect(jsonPath("$.body.questions[0].id").value(1))
                .andExpect(jsonPath("$.body.questions[0].type").value("OPERATOR"))
                .andExpect(jsonPath("$.body.questions[0].title").value("두 수의 합 구하기"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        verify(adminService, times(1)).questionList(0, "id", 0);
    }

    @Test
    void question_list_with_params_test() throws Exception {
        var listDTO = new QuestionResponse.AdminListDTO(
                List.of(), 2, "title", 1, 0
        );
        when(adminService.questionList(2, "title", 1)).thenReturn(listDTO);

        mockMvc.perform(get("/admin/questions")
                        .param("page", "2")
                        .param("order", "title")
                        .param("sort", "1")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.page").value(2))
                .andExpect(jsonPath("$.body.order").value("title"))
                .andExpect(jsonPath("$.body.sort").value(1))
                .andExpect(jsonPath("$.body.totalCount").value(0))
                .andExpect(jsonPath("$.body.questions.length()").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        verify(adminService, times(1)).questionList(2, "title", 1);
    }

    // =========================
    // 2) CREATE
    // =========================
    @Test
    void question_create_test() throws Exception {
        // 요청 DTO (더미 데이터 기반)
        var req = new QuestionRequest.DTO();
        req.setType("OPERATOR");
        req.setTitle("두 수의 합 구하기");
        req.setContent("문제 설명: 정수 a, b가 주어질 때, 두 수의 합을 반환...");
        req.setTestVariable("[{\"a\":2,\"b\":3},{\"a\":10,\"b\":15},{\"a\":-1,\"b\":5}]");
        req.setTestAnswer("[5,25,4]");

        // 서비스 응답 DTO
        Question created = Question.builder()
                .id(10)
                .type(QuestionType.OPERATOR)
                .title(req.getTitle())
                .content(req.getContent())
                .testVariable(req.getTestVariable())
                .testAnswer(req.getTestAnswer())
                .build();
        var resp = new QuestionResponse.DTO(created);

        when(adminService.questionCreate(any(QuestionRequest.DTO.class))).thenReturn(resp);

        mockMvc.perform(post("/admin/questions")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.questionId").value(10))
                .andExpect(jsonPath("$.body.questionType").value("OPERATOR"))
                .andExpect(jsonPath("$.body.title").value("두 수의 합 구하기"))
                .andExpect(jsonPath("$.body.content").value(req.getContent()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        verify(adminService, times(1)).questionCreate(any(QuestionRequest.DTO.class));
    }

    // =========================
    // 3) UPDATE
    // =========================
    @Test
    void question_update_test() throws Exception {
        int id = 2;

        var req = new QuestionRequest.DTO();
        req.setType("OPERATOR");
        req.setTitle("두 수의 차 구하기(수정)");
        req.setContent("문제 설명: 수정된 내용...");
        req.setTestVariable("[{\"a\":5,\"b\":3},{\"a\":10,\"b\":20},{\"a\":20,\"b\":5}]");
        req.setTestAnswer("[2,-10,15]");

        Question updated = Question.builder()
                .id(id)
                .type(QuestionType.OPERATOR)
                .title(req.getTitle())
                .content(req.getContent())
                .testVariable(req.getTestVariable())
                .testAnswer(req.getTestAnswer())
                .build();
        var resp = new QuestionResponse.DTO(updated);

        when(adminService.questionUpdate(any(QuestionRequest.DTO.class), eq(id))).thenReturn(resp);

        mockMvc.perform(put("/admin/questions/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.questionId").value(id))
                .andExpect(jsonPath("$.body.questionType").value("OPERATOR"))
                .andExpect(jsonPath("$.body.title").value("두 수의 차 구하기(수정)"))
                .andExpect(jsonPath("$.body.content").value(req.getContent()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document);

        verify(adminService, times(1)).questionUpdate(any(QuestionRequest.DTO.class), eq(id));
    }

    // =========================
    // 4) DELETE
    // =========================
    @Test
    void question_delete_test() throws Exception {
        int id = 3;
        doNothing().when(adminService).questionDelete(id);

        mockMvc.perform(delete("/admin/questions/{id}", id).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body", nullValue()));

        verify(adminService, times(1)).questionDelete(id);
    }
}
