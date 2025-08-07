package org.example.jjava_main.domain.user.admin;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;

}
