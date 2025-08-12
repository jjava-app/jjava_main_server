package org.example.jjava_main.domain.compile;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserRepository;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
import org.example.jjava_main.dto.QuestionResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CheckService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private static final String API_KEY = System.getenv("JJAVA_REFACTOR_API_KEY"); // 환경변수에서 API Key 읽기
    private static final String URL = "https://api.openai.com/v1/chat/completions";


    @Transactional
    public CheckResponse.PassDTO checkAndCodeRefactor(String userCode, Integer questionId, Integer userId) {
        // 문제 조회
        Question question = questionRepository.findById(questionId);
        if (question == null) throw new Exception404("문제를 찾을 수 없습니다.");

        RestTemplate restTemplate = new RestTemplate();

        // system 메시지 생성
        CheckRequest.RefactorDTO.Message system = new CheckRequest.RefactorDTO.Message();
        system.setRole("system");
        system.setContent("당신은 초급 개발자의 코드를 리팩토링해주는 멘토입니다.");

        // user 메시지 생성 (입력된 코드 기반)
        CheckRequest.RefactorDTO.Message messageUser = new CheckRequest.RefactorDTO.Message();
        messageUser.setRole("user");
        messageUser.setContent(
                "다음 JavaScript 코드를 리팩토링하고, 반드시 아래 형식으로 답변하세요.\n" +
                        "[CODE]\n리팩토링된 전체 코드\n[/CODE]\n" +
                        "[NOTE]\n개선 이유\n[/NOTE]\n" +
                        "코드:\n```java\n" + userCode + "\n```"
        );

        // 메시지 리스트 구성
        List<CheckRequest.RefactorDTO.Message> messages = List.of(system, messageUser);

        // 새 요청 객체 생성
        CheckRequest.RefactorDTO requestBody = new CheckRequest.RefactorDTO("gpt-3.5-turbo", messages, 0.7);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<CheckRequest.RefactorDTO> request = new HttpEntity<>(requestBody, headers);

        // Map으로 받기
        ResponseEntity<Map> response = restTemplate.postForEntity(URL, request, Map.class);

        // content 꺼내기
        String content = (String) ((Map) ((Map) ((List) response.getBody().get("choices"))
                .get(0)).get("message")).get("content");

        // 직접 분리
        String codePart = null;
        String notePart = null;
        if (content != null) {
            int codeStart = content.indexOf("[CODE]");
            int codeEnd = content.indexOf("[/CODE]");
            if (codeStart != -1 && codeEnd != -1) {
                codePart = content.substring(codeStart + 6, codeEnd).trim();
            }

            int noteStart = content.indexOf("[NOTE]");
            int noteEnd = content.indexOf("[/NOTE]");
            if (noteStart != -1 && noteEnd != -1) {
                notePart = content.substring(noteStart + 6, noteEnd).trim();
            }
        }

        // 4. DTO 리턴
        CheckResponse.PassDTO passDTO = new CheckResponse.PassDTO();
        passDTO.setRefactoredCode(codePart);
        passDTO.setRefactorNote(notePart);

        // user score update
        User user = userRepository.findById(userId).orElseThrow();
        if (user == null) throw new Exception404("존재하지 않는 회원입니다.");
        int score = 1;
        user.scoreUpdate(user.getScore() + score);

        // 결과 리턴 (refactoredCode, refactorNote 모두 포함됨)
        return passDTO;
    }


    public QuestionResponse.ListDTO questionListGet(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user == null) throw new Exception404("존재하지 않는 회원입니다.");

        // 1) 전체 문제 조회
        List<Question> questions = questionRepository.findAll();

        // 2) 유저가 푼 문제 조회
        List<SolvedQuestion> solvedList = questionRepository.findSolvedQuestionByUserId(userId);

        // 3) 응답 아이템 구성
        List<QuestionResponse.ListDTO.QuestionDTO> items = questions.stream()
                .map(q -> new QuestionResponse.ListDTO.QuestionDTO(
                        q.getId(),
                        q.getType().getName(),
                        q.getTitle()
                ))
                .toList();

        // solved questionId 리스트화
        List<Question> solvedQuestions = solvedList.stream()
                .map(SolvedQuestion::getQuestion) // 엔티티 getter 맞게 수정
                .toList();

        // 5) 총계 및 solvedCount
        int totalCount = questions.size();
        int solvedCount = solvedList.size();

        return new QuestionResponse.ListDTO(userId, totalCount, solvedCount, items, solvedQuestions);
    }

    public QuestionResponse.DetailDTO questionDetailGet(Integer questionId) {
        // 문제 조회
        Question question = questionRepository.findById(questionId);
        if (question == null) throw new Exception404("문제를 찾을 수 없습니다.");

        return new QuestionResponse.DetailDTO(questionId, question.getTitle(), question.getContent());
    }
}
