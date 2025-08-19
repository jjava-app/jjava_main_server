package org.example.jjava_main.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.example.jjava_main.dto.UserResponse.LevelUpdateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponse.DTO userGet(User user) {

        int rank = userRepository.findRankByScoreAndId(user.getScore());

        return new UserResponse.DTO(user, rank);
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
