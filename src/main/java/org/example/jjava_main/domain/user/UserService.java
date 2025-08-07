package org.example.jjava_main.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUser(User user) {
        return new UserResponse(user);
    }


    @Transactional
    public UserResponse updateLevel(UserRequest.UpdateLevelDTO reqDTO, User user) {
        userRepository.updateLevelById(reqDTO.getLevel(), user.getId());

        // user 객체 그대로 응답에 사용
        return new UserResponse(user);
    }
}
