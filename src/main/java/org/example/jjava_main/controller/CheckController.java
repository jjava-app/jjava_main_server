package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.compile.CheckService;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
import org.example.jjava_main.dto.QuestionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CheckController {

    private final HttpUtil httpUtil;
    private final CheckService checkService;

    @PostMapping("/check")
    public ResponseEntity<?> checkProxyAndCodeRefactor(@RequestParam Integer questionId, @RequestBody CheckRequest.DTO reqDTO) {
        // given
        Integer userId = 2;

        // 성공이면 PassDTO, 실패면 List<FailDTO>가 돌아옴
        Object respDTO = httpUtil.checkServerSend(reqDTO, userId, questionId);

        if (respDTO instanceof org.example.jjava_main.dto.CheckResponse.PassDTO pass) {
            // passed == true 일 때만 리팩토링
            if (pass.isPassed()) {
                try {
                    // AI 리팩토링 요청
                    CheckResponse.PassDTO aiResult = checkService.checkAndCodeRefactor(pass.getCode(), questionId, userId);

                    // AI 응답에서 값 세팅
                    pass.setRefactoredCode(aiResult.getRefactoredCode());
                    pass.setRefactorNote(aiResult.getRefactorNote());
                } catch (Exception e) {
                    log.warn("AI refactor failed: {}", e.getMessage(), e);
                    // AI 호출 실패 시 안전 처리
                    pass.setRefactorNote("자동 리팩토링 실패: " + e.getMessage());
                }
            }
            return Resp.ok(pass);
        }

        // 실패(List<FailDTO>)면 그대로
        return Resp.ok(respDTO);
    }


    // 문제 리스트
    @GetMapping("/questions")
    public ResponseEntity<?> questionListGet(@RequestParam Integer userId) {
        QuestionResponse.ListDTO respDTO = checkService.questionListGet(userId);
        return Resp.ok(respDTO);
    }

    // 문제 상세보기
    @GetMapping("/questions/{id}")
    public ResponseEntity<?> questionDetailGet(@PathVariable("id") Integer questionId) {
        QuestionResponse.DetailDTO respDTO = checkService.questionDetailGet(questionId);
        return Resp.ok(respDTO);
    }

}
