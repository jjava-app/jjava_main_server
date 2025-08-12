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

import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 적용 안 함
class AdminControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @MockBean private UserRepository userRepository;

    @MockBean private AdminService adminService; // 컨트롤러가 주입받는 서비스만 Mock

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
        var dto = new UserResponse.ListDTO(java.util.List.of(UserResponse.UserDTO.from(mockUser)), 0, "", 1, 0);
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
    void updateUser_test() {
        // given
        User user = User.builder()
                .id(1) // 테스트용 세터/빌더로 세팅 가능해야 함
                .username("ssar")
                .email("ssar@nate.com")
                .role(UserRole.USER)
                .score(100)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        var req = new UserRequest.UserUpdateDTO("ssar", "ssar@nate.com", UserRole.USER, 100);

        // when
        var resp = adminService.userUpdate(1, req);

        // then
        System.out.println("응답 : " + resp);
    }
}
