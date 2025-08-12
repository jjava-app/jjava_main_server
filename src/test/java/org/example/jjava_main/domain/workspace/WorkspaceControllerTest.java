package org.example.jjava_main.domain.workspace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jjava_main._core.error.ex.Exception403;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.controller.WorkspaceController;
import org.example.jjava_main.domain.block.BlockLibrary;
import org.example.jjava_main.domain.user.*;
import org.example.jjava_main.dto.WorkspaceRequest;
import org.example.jjava_main.dto.WorkspaceResponse;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkspaceController.class)
@Import({WorkspaceControllerTest.TestConfig.class, WorkspaceControllerTest.TestSecurityConfig.class})
public class WorkspaceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @MockBean private WorkspaceService workspaceService;

    private User mockUser;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

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
    void set_up_security_context() {
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
    }

    @Test
    void get_workspace_list() throws Exception {
        Workspace workspace = Workspace.builder().id(1).title("워크스페이스1").userId(1).build();
        when(workspaceService.workspaceList(1)).thenReturn(List.of(workspace));

        mockMvc.perform(get("/workspace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].title").value("워크스페이스1"));
    }

    @Test
    void get_workspace_detail() throws Exception {
        Workspace workspace = Workspace.builder().id(1).title("워크스페이스1").userId(1).build();
        when(workspaceService.workspaceDetail(1, 1)).thenReturn(workspace);

        mockMvc.perform(get("/workspace/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.title").value("워크스페이스1"));
    }

    @Test
    void get_workspace_detail_not_found() throws Exception {
        when(workspaceService.workspaceDetail(999, 1))
                .thenThrow(new Exception404("해당 워크스페이스가 존재하지 않습니다."));

        mockMvc.perform(get("/workspace/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_workspace_detail_forbidden() throws Exception {
        when(workspaceService.workspaceDetail(1, 1))
                .thenThrow(new Exception403("해당 워크스페이스의 소유자가 아닙니다."));

        mockMvc.perform(get("/workspace/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_workspace() throws Exception {
        Workspace workspace = Workspace.builder()
                .id(1)
                .userId(1)
                .title("새 워크스페이스")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        WorkspaceResponse.CreateDTO dto = new WorkspaceResponse.CreateDTO(workspace);
        when(workspaceService.workspaceCreate(1)).thenReturn(dto);

        mockMvc.perform(post("/workspace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.title").value("새 워크스페이스"))
                .andExpect(jsonPath("$.body.userId").value(1))
                .andExpect(jsonPath("$.body.createdAt").exists());
    }

    @Test
    void update_workspace() throws Exception {
        WorkspaceRequest.UpdateDTO reqDTO = new WorkspaceRequest.UpdateDTO();
        reqDTO.setTitle("업데이트된 제목");
        reqDTO.setSerializedJson("{\"blocks\":[]}");
        reqDTO.setLibraryJson("{\"extensions\":[]}");

        Workspace workspace = Workspace.builder()
                .id(1)
                .userId(1)
                .title(reqDTO.getTitle())
                .serializedJson(reqDTO.getSerializedJson())
                .build();

        BlockLibrary blockLibrary = BlockLibrary.builder()
                .id(1)
                .userId(1)
                .libraryJson(reqDTO.getLibraryJson())
                .build();

        WorkspaceResponse.DTO respDTO = new WorkspaceResponse.DTO(workspace, blockLibrary);

        when(workspaceService.workspaceUpdate(eq(1), any(), eq(1))).thenReturn(respDTO);

        mockMvc.perform(put("/workspace/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reqDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.title").value("업데이트된 제목"))
                .andExpect(jsonPath("$.body.serializedJson").value("{\"blocks\":[]}"))
                .andExpect(jsonPath("$.body.libraryJson").value("{\"extensions\":[]}"))
                .andExpect(jsonPath("$.body.userId").value(1))
                .andExpect(jsonPath("$.body.id").value(1));
    }

    @Test
    void update_workspace_not_found() throws Exception {
        WorkspaceRequest.UpdateDTO reqDTO = new WorkspaceRequest.UpdateDTO();
        reqDTO.setTitle("업데이트된 제목");
        reqDTO.setSerializedJson("{\"blocks\":[]}");

        when(workspaceService.workspaceUpdate(eq(1), any(), eq(1)))
                .thenThrow(new Exception404("해당 워크스페이스가 존재하지 않습니다."));

        mockMvc.perform(put("/workspace/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reqDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_workspace_forbidden() throws Exception {
        WorkspaceRequest.UpdateDTO reqDTO = new WorkspaceRequest.UpdateDTO();
        reqDTO.setTitle("업데이트된 제목");
        reqDTO.setSerializedJson("{\"blocks\":[]}");

        when(workspaceService.workspaceUpdate(eq(1), any(), eq(1)))
                .thenThrow(new Exception403("해당 워크스페이스의 소유자가 아닙니다."));

        mockMvc.perform(put("/workspace/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reqDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_workspace() throws Exception {
        doNothing().when(workspaceService).workspaceDelete(1, 1);

        mockMvc.perform(delete("/workspace/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").doesNotExist());
    }

    @Test
    void delete_workspace_not_found() throws Exception {
        doThrow(new Exception404("해당 워크스페이스가 존재하지 않습니다."))
                .when(workspaceService).workspaceDelete(1, 1);

        mockMvc.perform(delete("/workspace/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_workspace_forbidden() throws Exception {
        doThrow(new Exception403("해당 워크스페이스의 소유자가 아닙니다."))
                .when(workspaceService).workspaceDelete(1, 1);

        mockMvc.perform(delete("/workspace/1"))
                .andExpect(status().isForbidden());
    }
}
