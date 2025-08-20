package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.compile.CheckService;
import org.example.jjava_main.domain.leaderboard.LeaderboardService;
import org.example.jjava_main.domain.question.QuestionService;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserService;
import org.example.jjava_main.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HomeController {
    private final UserService userService;
    private final LeaderboardService leaderboardService;
    private final CheckService checkService;

    @GetMapping("/home")
    public ResponseEntity<?> home(@AuthenticationPrincipal User user) {
        UserResponse.DTO userDTO = userService.userGet(user);
        LeaderboardResponse.DTO leaderboardDTO = leaderboardService.top10List();
        // TODO 1 : 지난 학습 리스트 3개만 잡아서 DTO로 빼기
        QuestionResponse.HomeDTO sqDTO = checkService.solvedQuestionListLimit3(user);
        // TODO 2 : 3개를 묶은 홈 DTO 만들어서 반환하기
        var respDTO = new HomeResponse.DTO(userDTO, leaderboardDTO, sqDTO);
        return Resp.ok(respDTO);
    }
}
