package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.auth.AuthService;
import org.example.jjava_main.domain.user.UserService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

}
