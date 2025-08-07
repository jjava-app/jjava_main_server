package org.example.jjava_main.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/user")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal User user) {
        var respDTO = userService.getUser(user);
        return Resp.ok(respDTO);
    }

    @PutMapping("/user/level")
    public ResponseEntity<?> updateLevel(@AuthenticationPrincipal User user, @RequestBody UserRequest.UpdateLevelDTO reqDTO) {
        var respDTO = userService.updateLevel(reqDTO, user);
        return Resp.ok(respDTO);
    }
}
