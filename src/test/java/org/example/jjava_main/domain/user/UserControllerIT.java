//package org.example.jjava_main.domain.user;
//
//import org.example.jjava_main.MyRestDoc;
//import org.example.jjava_main.domain.auth.AuthService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Sql(scripts = {"/db/clear.sql", "/db/data.sql"},
//        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//class UserControllerIT extends MyRestDoc {
//
//    @MockBean
//    AuthService authService;
//
//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    UserRepository userRepository; // EntityManager 기반 레포지토리
//
//    Authentication auth;
//
//    private User seedUser;
//
//    @BeforeEach
//    void setUp() {
//        // 더미에 맞는 이메일로 조회 (id 하드코딩 회피)
//        seedUser = userRepository.findByEmail("haha1234@nate.com")
//                .orElseThrow(() -> new IllegalStateException("seed user not found in data.sql"));
//        auth = new UsernamePasswordAuthenticationToken(seedUser, null, seedUser.getAuthorities());
//    }
//
//    @Test
//    void get_my_page_profile_success() throws Exception {
//        mockMvc.perform(get("/users/mypage").with(authentication(auth)))
//                .andDo(print()) // 요청/응답 전체 출력
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.id").value(seedUser.getId()))
//                .andExpect(jsonPath("$.body.email").value("haha1234@nate.com"))
//                .andExpect(jsonPath("$.body.username").value("haha"))
//                .andExpect(jsonPath("$.body.level").value("INTERMEDIATE"))
//                .andExpect(jsonPath("$.body.score").value(45))
//                .andExpect(jsonPath("$.body.rank").value(4))
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document);
//    }
//
//    @Test
//    void update_user_level_success() throws Exception {
//        String req = """
//                    {"level":"EXPERT","username":"haha-up"}
//                """;
//
//        mockMvc.perform(put("/users/mypage/level")
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(req)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.username").value("haha-up"))
//                .andExpect(jsonPath("$.body.level").value("EXPERT"))
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document);
//    }
//}
