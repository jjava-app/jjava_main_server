package org.example.jjava_main.domain.user.admin;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
