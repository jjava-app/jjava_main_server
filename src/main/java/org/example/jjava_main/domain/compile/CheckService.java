package org.example.jjava_main.domain.compile;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.error.ex.Exception403;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.question.ProgressStatus;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CheckService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private static final String API_KEY = System.getenv("JJAVA_REFACTOR_API_KEY"); // 환경변수에서 API Key 읽기
    private static final String URL = "https://api.openai.com/v1/chat/completions";


    @Transactional
    public CheckResponse.PassDTO checkAndCodeRefactor(String userCode, Integer questionId, Integer userId, String serializedJson, String blockExtensionJson) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new Exception404("존재하지 않는 회원입니다."));

        if (!userPS.getId().equals(userId)) throw new Exception403("권한이 없습니다.");

        // 문제 조회
        Question questionPS = questionRepository.findById(questionId).orElseThrow(() -> new Exception404("문제를 찾을 수 없습니다."));

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

        // user score update
        int score = 1;
        userPS.scoreUpdate(userPS.getScore() + score);

        // 3) SolvedQuestion upsert + REVIEWED 전환 + aiComment 저장
        SolvedQuestion sq = questionRepository
                .findSolvedQuestionByUserIdQuestionId(userId, questionId)
                .orElseGet(() -> SolvedQuestion.builder()
                        .user(userPS)
                        .question(questionPS)
                        .progressStatus(ProgressStatus.IN_PROGRESS) // 최초 생성 시점값
                        .build());

        if (serializedJson != null) sq.setSerializedJson(serializedJson);
        if (blockExtensionJson != null) sq.setBlockExtensionJson(blockExtensionJson);
        sq.updateProgressStatus(ProgressStatus.REVIEWED);
        sq.updateAiComment(content); // 전체 응답 저장(원하면 notePart로 교체)


        questionRepository.createSolvedQuestion(sq); // 저장/업데이트

        // 4. DTO 리턴
        CheckResponse.PassDTO passDTO = new CheckResponse.PassDTO();
        passDTO.setRefactoredCode(codePart);
        passDTO.setRefactorNote(notePart);


        // 결과 리턴 (refactoredCode, refactorNote 모두 포함됨)
        return passDTO;
    }


    public QuestionResponse.ListDTO questionListGet(Integer userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new Exception404("존재하지 않는 회원입니다."));

        if (!userPS.getId().equals(userId)) throw new Exception403("권한이 없습니다.");

        // 1) 전체 문제 조회
        List<Question> questions = questionRepository.findAll();

        // 2) 유저가 푼 문제 조회
        List<SolvedQuestion> solvedList = questionRepository.findSolvedQuestionByUserId(userPS.getId());

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

        return new QuestionResponse.ListDTO(userPS.getId(), totalCount, solvedCount, items, solvedQuestions);
    }

    public QuestionResponse.DetailDTO questionDetailGet(Integer questionId) {
        // 문제 조회
        Question questionPS = questionRepository.findById(questionId).orElse(null);
        if (questionPS == null) throw new Exception404("문제를 찾을 수 없습니다.");

        return new QuestionResponse.DetailDTO(questionPS.getId(), questionPS.getTitle(), questionPS.getContent());
    }


    // 푼 문제 저장 - 상태: IN_PROGRESS
    @Transactional
    public QuestionResponse.SolvedQuestionCreateDTO solvedQuestionUpsert(Integer userId, Integer questionId, String serializedJson, String blockExtensionJson) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new Exception404("존재하지 않는 회원입니다."));
        if (!userPS.getId().equals(userId)) throw new Exception403("권한이 없습니다.");

        Question questionPS = questionRepository.findById(questionId).orElse(null);
        if (questionPS == null) throw new Exception404("문제를 찾을 수 없습니다.");

        SolvedQuestion solvedQuestionPS = questionRepository.findSolvedQuestionByUserIdQuestionId(userPS.getId(), questionId)
                .map(sq -> {
                    // 존재하면 필드 갱신
                    if (serializedJson != null && blockExtensionJson != null)
                        sq.updateJson(serializedJson, blockExtensionJson);
                    return sq;
                }).orElseGet(() -> SolvedQuestion.builder()
                        .user(userPS)
                        .question(questionPS)
                        .serializedJson(serializedJson)
                        .blockExtensionJson(blockExtensionJson)
                        .progressStatus(ProgressStatus.IN_PROGRESS)
                        .build());

        SolvedQuestion newSolvedQuestion = questionRepository.createSolvedQuestion(solvedQuestionPS);

        QuestionResponse.SolvedQuestionCreateDTO respDTO = new QuestionResponse.SolvedQuestionCreateDTO(newSolvedQuestion.getId(), newSolvedQuestion.getUser().getId(), newSolvedQuestion.getQuestion().getId(), newSolvedQuestion.getSerializedJson(), newSolvedQuestion.getBlockExtensionJson(), newSolvedQuestion.getProgressStatus());

        return respDTO;
    }

    public QuestionResponse.SolvedQuestionDetailDTO solvedQuestionDetailGet(Integer solvedQuestionId) {
        SolvedQuestion solvedQuestion = questionRepository.findSolvedQuestionById(solvedQuestionId)
                .orElseThrow(() -> new Exception404("해당 유저가 푼 문제가 아닙니다."));

        Question question =  questionRepository.findById(solvedQuestionId).orElse(null);
        if(question == null) throw new Exception404("해당하는 문제가 없습니다.");

        // TODO 2 : DTO 반환
        return new QuestionResponse.SolvedQuestionDetailDTO(solvedQuestion);
    }

    // 내가 푼 문제 리스트
    public QuestionResponse.SolvedQuestionListDTO solvedQuestionListGet(Integer userId) {

        // 1. 유저가 푼 문제 가져오기
        List<SolvedQuestion> solvedQuestions = questionRepository.findSolvedQuestionByUserId(userId);

        // 2. 타입별로 그룹핑 후 DTO로 변환
        return new QuestionResponse.SolvedQuestionListDTO(solvedQuestions);
    }


    public QuestionResponse.HomeDTO solvedQuestionListLimit3(User user) {
        // 1. 유저가 푼 문제 가져오기
        List<SolvedQuestion> solvedQuestions = questionRepository.findSolvedQuestionByUserId(user.getId());

        // DTO 반환
        return new QuestionResponse.HomeDTO(solvedQuestions);
    }
}
