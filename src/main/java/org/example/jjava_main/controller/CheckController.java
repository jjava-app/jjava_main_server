package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.compile.CheckService;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
import org.example.jjava_main.dto.QuestionRequest;
import org.example.jjava_main.dto.QuestionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CheckController {

    private final HttpUtil httpUtil;
    private final CheckService checkService;

    @PostMapping("/check")
    public ResponseEntity<?> checkProxyAndCodeRefactor(@AuthenticationPrincipal User user, @RequestParam Integer questionId, @RequestBody CheckRequest.DTO reqDTO) {
        // 성공이면 PassDTO, 실패면 List<FailDTO>가 돌아옴
        Object respDTO = httpUtil.checkServerSend(reqDTO, user.getId(), questionId);

        if (respDTO instanceof org.example.jjava_main.dto.CheckResponse.PassDTO pass) {
            // passed == true 일 때만 리팩토링
            if (pass.isPassed()) {
                try {
                    // AI 리팩토링 요청
                    CheckResponse.PassDTO aiResult = checkService.checkAndCodeRefactor(pass.getCode(), questionId, user.getId(), reqDTO.getSerializedJson(), reqDTO.getBlockExtensionJson());

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
    public ResponseEntity<?> questionListGet(@AuthenticationPrincipal User user) {
        QuestionResponse.ListDTO respDTO = checkService.questionListGet(user.getId());
        return Resp.ok(respDTO);
    }

    // 문제 상세보기
    @GetMapping("/questions/{id}")
    public ResponseEntity<?> questionDetailGet(@PathVariable("id") Integer questionId) {
        QuestionResponse.DetailDTO respDTO = checkService.questionDetailGet(questionId);
        return Resp.ok(respDTO);
    }

    // 문제 저장 (문제 파일 전 중간 저장)
    @PutMapping("/solved-questions/{questionId}")
    public ResponseEntity<?> solvedQuestionUpsert(@AuthenticationPrincipal User user, @PathVariable("questionId") Integer questionId, @RequestBody QuestionRequest.SolvedQuestionCreateDTO reqDTO) {

        QuestionResponse.SolvedQuestionCreateDTO respDTO = checkService.solvedQuestionUpsert(user.getId(), questionId, reqDTO.getSerializedJson(), reqDTO.getBlockExtensionJson());

        return Resp.ok(respDTO);
    }

    @GetMapping("/solved-questions/{id}")
    public ResponseEntity<?> solvedQuestionDetailGet(@AuthenticationPrincipal User user, @PathVariable("id") Integer solvedQuestionId) {
        QuestionResponse.SolvedQuestionDetailDTO respDTO = checkService.solvedQuestionDetailGet(solvedQuestionId);
        // TODO 3 : body에 DTO 담기
        return Resp.ok(respDTO);
    }

    // 내가 푼 문제 리스트
    @GetMapping("/solved-questions/list")
    public ResponseEntity<?> solvedQuestionListGet(@AuthenticationPrincipal User user) {
        int userId = user.getId();

        QuestionResponse.SolvedQuestionListDTO respDTO = checkService.solvedQuestionListGet(userId);
        return Resp.ok(respDTO);
    }

}
