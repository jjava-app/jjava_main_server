package org.example.jjava_main.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponse.DTO userGet(User user) {
        User userPS = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception404("해당하는 유저가 없습니다."));
        int rank = userRepository.findRankByScoreAndId(userPS.getScore());

        return new UserResponse.DTO(userPS, rank);
    }


    @Transactional
    public UserResponse.UpdateDTO userUpdate(UserRequest.LevelUpdateDTO reqDTO, User user) {
        User userPS = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception404("해당하는 유저가 없습니다."));

        userPS.userUpdate(reqDTO.getLevel(), reqDTO.getUsername());

        // user 객체 그대로 응답에 사용
        return new UserResponse.UpdateDTO(userPS);
    }
}
