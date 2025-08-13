package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.user.admin.AdminService;
import org.example.jjava_main.dto.UserRequest;
import org.example.jjava_main.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AdminService adminService;

    // 페이징, 분류 (id, 이름, 점수, 이메일), sort (0 = 오름차순, 1 = 내림차순)
    @GetMapping("/users")
    public ResponseEntity<?> userList(@RequestParam(required = false, defaultValue = "0") int page,
                                      @RequestParam(required = false, defaultValue = "id") String order,
                                      @RequestParam(required = false, defaultValue = "0") int sort) {
        UserResponse.ListDTO respDTO = adminService.userList(page, order, sort);
        return Resp.ok(respDTO);
    }

    // 회원 수정 - Min
    @PostMapping("/update/{id}")
    public ResponseEntity<?> userUpdate(@PathVariable("id") Integer id, @RequestBody UserRequest.UserUpdateDTO reqDTO){
        UserResponse.UserUpdateDTO respDTO = adminService.userUpdate(id, reqDTO);
        return Resp.ok(respDTO);
    }

    // 회원 삭제


    // 회원 추가 (필요 시)
}
