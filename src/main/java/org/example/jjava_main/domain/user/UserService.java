package org.example.jjava_main.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.example.jjava_main.dto.UserResponse.LevelUpdateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponse userGet(User principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new Exception404("유저를 찾을 수 없습니다."));

        int safeScore = Optional.ofNullable(user.getScore()).orElse(0);
        int rank = userRepository.findRankByScoreAndId(user.getScore());
        return new UserResponse(user, rank);
    }


    @Transactional
    public LevelUpdateResponse levelUpdate(UserRequest.LevelUpdateDTO reqDTO, User user) {
        User userPS = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException());

        userPS.userUpdate(reqDTO.getLevel(), reqDTO.getUsername());

        // user 객체 그대로 응답에 사용
        return new LevelUpdateResponse(userPS);
    }
}
