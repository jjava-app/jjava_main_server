package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRequest;
import org.example.jjava_main.domain.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/mypage")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal User user) {
        var respDTO = userService.userGet(user);
        return Resp.ok(respDTO);
    }

    @PutMapping("/mypage/level")
    public ResponseEntity<?> updateLevel(@AuthenticationPrincipal User user, @RequestBody UserRequest.LevelUpdateDTO reqDTO) {
        var respDTO = userService.levelUpdate(reqDTO, user);
        return Resp.ok(respDTO);
    }
}
