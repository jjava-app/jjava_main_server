package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
}
