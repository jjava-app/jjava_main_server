package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.leaderboard.LeaderboardService;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserService;
import org.example.jjava_main.dto.LeaderboardResponse;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HomeController {
    private final UserService userService;
    private final LeaderboardService leaderboardService;

    @GetMapping("/home")
    public ResponseEntity<?> home(@AuthenticationPrincipal User user) {
        UserResponse.DTO userDTO = userService.userGet(user);
        LeaderboardResponse.DTO leaderboardDTO = leaderboardService.top10List();
        // TODO 1 : 지난 학습 리스트 3개만 잡아서 DTO로 빼기
        
        // TODO 2 : 3개를 묶은 홈 DTO 만들어서 반환하기
        
        return Resp.ok(null);
    }
}
