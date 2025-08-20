package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.user.admin.AdminService;
import org.example.jjava_main.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    @PutMapping("/users/{id}")
    public ResponseEntity<?> userUpdate(@PathVariable("id") Integer id, @RequestBody UserRequest.UserUpdateDTO reqDTO){
        UserResponse.UserUpdateDTO respDTO = adminService.userUpdate(id, reqDTO);
        log.info("Admin : " + respDTO.getUsername() + "님의 정보를 수정하였습니다 : " + reqDTO);
        return Resp.ok(respDTO);
    }

    // 회원 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> userDelete(@PathVariable("id") Integer id) {
        adminService.userDelete(id);
        log.info("Admin : 회원을 삭제했습니다 : User_ID = " + id);
        return Resp.ok(null);
    }

    @GetMapping("/questions")
    public ResponseEntity<?> questionList(@RequestParam(required = false, defaultValue = "0") int page,
                                          @RequestParam(required = false, defaultValue = "id") String order,
                                          @RequestParam(required = false, defaultValue = "0") int sort) {
        QuestionResponse.AdminListDTO respDTO = adminService.questionList(page, order, sort);
        return Resp.ok(respDTO);
    }

    @PostMapping("/questions")
    public ResponseEntity<?> questionCreate(@RequestBody QuestionRequest.DTO reqDTO) {
        QuestionResponse.DTO respDTO = adminService.questionCreate(reqDTO);
        return Resp.ok(respDTO);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> questionUpdate(@PathVariable("id") Integer id, @RequestBody QuestionRequest.DTO reqDTO) {
        QuestionResponse.DTO respDTO = adminService.questionUpdate(reqDTO, id);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> questionDelete(@PathVariable("id") Integer id) {
        adminService.questionDelete(id);
        return Resp.ok(null);
    }
}
