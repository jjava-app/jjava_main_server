package org.example.jjava_main.domain.user.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;

    public UserResponse.ListDTO userList(int page, String order, int sort) {
        // order에 따른 구분 (default id)
        List<User> userList = new ArrayList<>();

        if(order.equals("id")) userList = userRepository.findAllOrderById(page, sort);
        else if(order.equals("name")) userList = userRepository.findAllOrderByName(page, sort);
        else if(order.equals("email")) userList = userRepository.findAllOrderByEmail(page, sort);
        else if(order.equals("score")) userList = userRepository.findAllOrderByScore(page, sort);
        else throw new Exception404("해당하는 조건을 찾을 수 없습니다.");

        Long totalCount = userRepository.getTotalCount();

        List<UserResponse.UserDTO> dtoList = userList.stream()
                .map(UserResponse.UserDTO::from)
                .toList();

        return new UserResponse.ListDTO(dtoList, page, order, totalCount.intValue(), sort);

    }

    //회원 수정 - min

    // Service
    @Transactional
    public UserResponse.UserUpdateDTO userUpdate(Integer id, UserRequest.UserUpdateDTO reqDTO) {
        User userPS = userRepository.findById(id)
                .orElseThrow(() -> new Exception404("유저가 존재하지 않습니다."));
        // email, username, role, score
        userPS.adminUpdate(reqDTO.getEmail(), reqDTO.getUsername(), reqDTO.getRole(), reqDTO.getScore());
        return new UserResponse.UserUpdateDTO(userPS);
    }
}
