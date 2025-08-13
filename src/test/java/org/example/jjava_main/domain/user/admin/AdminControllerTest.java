package org.example.jjava_main.domain.user.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main.MyRestDoc;
import org.example.jjava_main.controller.AdminController;
import org.example.jjava_main.domain.user.*;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
}
