package org.example.jjava_main.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.auth.provider.UserAccountProvider;
import org.example.jjava_main.domain.auth.provider.UserAccountProviderRepository;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserAccountProviderRepository userAccountProviderRepository;

    public UserResponse.DTO userGet(User user) {
        User userPS = userRepository.findById(user.getId())
                .orElseThrow(() -> new Exception404("해당하는 유저가 없습니다."));

        int safeScore = Optional.ofNullable(userPS.getScore()).orElse(0);
        int rank = userRepository.findRankByScoreAndId(userPS.getScore());

        UserResponse.DTO resp = new UserResponse.DTO(userPS, rank);

        List<UserAccountProvider> links = userAccountProviderRepository.findAllByUserId(userPS.getId());
        List<UserResponse.LinkedAccountDTO> linked = links.stream()
                .map(uap -> new UserResponse.LinkedAccountDTO(
                        // providerType을 프론트 요구대로 소문자 문자열로
                        uap.getProvider().getProviderType().name().toLowerCase(),
                        // null 안전
                        Optional.ofNullable(uap.getEmail()).orElse("")
                ))
                .toList();


        resp.setLinked(linked);
        return resp;
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
