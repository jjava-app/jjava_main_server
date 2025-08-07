package org.example.jjava_main.domain.auth;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
}
