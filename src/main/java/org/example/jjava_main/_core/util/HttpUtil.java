package org.example.jjava_main._core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class HttpUtil {

    private final WebClient webClient;
    private final QuestionRepository questionRepository;

    public HttpUtil(QuestionRepository questionRepository) {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
        this.questionRepository = questionRepository;
    }


    // 컴파일 서버 주소
    private static final String BASE_URL = "http://localhost:8081"; // 컴파일 서버 주소
    private static final String COMPILE_ENDPOINT = "/compile";
    private static final String CHECK_ENDPOINT = "/check";


    /**
     * 프론트에서 받은 코드를 컴파일 서버로 전송
     * <p>
     * reqDTO: 프론트에서 받은 요청(JSON)
     * return: 컴파일 서버 응답(JSON String)
     */
    public CompileResponse.DTO compileServerSend(CompileRequest.DTO reqDTO, Integer userId) {
        try {
            Resp<CompileResponse.DTO> resp = webClient.post()
                    .uri(COMPILE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(reqDTO)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Resp<CompileResponse.DTO>>() {
                    }) // JSON 문자열로 받음 (Resp<CompileResponse.DTO>로 매핑)
                    .block(); // 동기 방식으로 결과 반환

            if (resp == null) {
                throw new RuntimeException("컴파일 서버 응답이 없습니다.");
            }

            if (resp.getStatus() != 200) {
                // 실패 응답이지만 통신은 정상 → 프론트로 그대로 전달
                return new CompileResponse.DTO(userId, null, resp.getMsg());
            }

            // 컴파일 서버 응답에 userId를 추가
            CompileResponse.DTO respDTO = resp.getBody();
            respDTO.setUserId(userId);  // 응답에 userId 세팅

            return respDTO;
        } catch (Exception e) {
            log.error("컴파일 서버 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("컴파일 서버와 통신 중 오류 발생");
        }
    }


    /**
     * 컴파일 서버로 코드 검증 요청을 전송하고, 응답을 PassDTO 또는 FailDTO 목록으로 변환하여 반환.
     */
    public Object checkServerSend(CheckRequest.DTO reqDTO, Integer userId, Integer questionId) {
        try {
            ObjectMapper om = new ObjectMapper();

            // 문제 조회해서 test_variable / test_answer 읽기
            Question q = questionRepository.findById(questionId)
                    .orElseThrow(() -> new Exception404("문제를 찾을 수 없습니다."));

            // DB JSON을 tests 배열로 변환
            List<CheckRequest.DTO.TestSpecDTO> tests = buildTestsFromQuestion(q, om);

            // 프론트에서 온 reqDTO를 그대로 보내지 말고,
            // 메인서버가 tests를 채워 넣은 outbound로 교체해서 전송
            CheckRequest.DTO outbound = new CheckRequest.DTO(reqDTO.getType(), reqDTO.getPayload(), tests);


            // 1. WebClient로 컴파일 서버의 /check 엔드포인트 호출
            // - resp: Resp<JsonNode> 형식의 응답 (성공/실패 모두 JSON으로 받음)
            Resp<JsonNode> resp = webClient.post()
                    .uri(CHECK_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(outbound)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Resp<JsonNode>>() {
                    })
                    .block(java.time.Duration.ofSeconds(10));  // 타임아웃 5초

            // 2. 응답 본문이 없으면 예외 처리
            if (resp == null || resp.getBody() == null) {
                throw new RuntimeException("컴파일 서버 응답이 없습니다.");
            }

            JsonNode body = resp.getBody();

            // 1) 배열 응답: 성공/실패 혼재 가능
            if (body.isArray()) {
                java.util.List<CheckResponse.FailDTO> failures = new java.util.ArrayList<>();

                for (JsonNode node : body) {
                    // 우선 passed 플래그가 있으면 그걸 신뢰
                    boolean passed = node.has("passed") && node.get("passed").asBoolean();

                    // passed 없으면 expected vs result로 판정
                    if (!passed && node.has("expected") && node.has("result")) {
                        String expectedStr = node.get("expected").asText();
                        String resultStr = node.get("result").asText();
                        passed = java.util.Objects.equals(expectedStr, resultStr);
                    }

                    if (!passed) {
                        CheckResponse.FailDTO f = om.convertValue(node, CheckResponse.FailDTO.class);
                        f.setUserId(userId);
                        f.setQuestionId(questionId);
                        f.setPassed(false);
                        failures.add(f);
                    }
                }

                // 실패가 하나라도 있으면 실패 리스트 반환
                if (!failures.isEmpty()) {
                    return failures; // List<FailDTO>
                }

                // 전부 통과 → PassDTO 만들어서 반환
                CheckResponse.PassDTO pass = new CheckResponse.PassDTO();
                pass.setPassed(true);
                pass.setUserId(userId);
                pass.setQuestionId(questionId);
                pass.setCode(reqDTO.getPayload());
                return pass;
            }

            // 2) 객체 응답: 단일 성공/실패
            boolean passedFlag = body.has("passed") && body.get("passed").asBoolean();
            if (!passedFlag) {
                CheckResponse.FailDTO f = om.convertValue(body, CheckResponse.FailDTO.class);
                f.setUserId(userId);
                f.setQuestionId(questionId);
                f.setPassed(false);
                return java.util.List.of(f); // 단일 실패도 리스트로
            }

            // 3) 단일 성공
            CheckResponse.PassDTO pass = om.convertValue(body, CheckResponse.PassDTO.class);
            pass.setUserId(userId);
            pass.setQuestionId(questionId);
            if (pass.getCode() == null) pass.setCode(reqDTO.getPayload());
            return pass;


        } catch (Exception e) {
            log.error("컴파일 서버 요청 실패: {}", e.getMessage(), e);
            // 7. 통신 실패 시에도 프론트가 일관되게 처리할 수 있도록
            //    FailDTO 한 건을 리스트로 감싸 반환
            return java.util.List.of(
                    new CheckResponse.FailDTO(
                            userId,
                            questionId,
                            false,
                            null,                 // index 미확정
                            null,                 // failedInputs 없음
                            "컴파일 서버와 통신 중 오류 발생",
                            null,                 // expected 없음
                            null                  // result 없음
                    )
            );
        }
    }

    /**
     * DB의 test_variable/test_answer(JSON 문자열)를 컴파일서버용 tests 배열로 변환
     */
    private List<CheckRequest.DTO.TestSpecDTO> buildTestsFromQuestion(Question q, ObjectMapper om) throws Exception {
        JsonNode vars = om.readTree(q.getTestVariable());   // e.g. [{"a":2,"b":3}, ...]
        JsonNode answers = om.readTree(q.getTestAnswer());  // e.g. [5,25,4]
        if (!vars.isArray() || !answers.isArray()) {
            throw new IllegalArgumentException("test_variable / test_answer는 배열이어야 합니다.");
        }
        int n = Math.min(vars.size(), answers.size());
        List<CheckRequest.DTO.TestSpecDTO> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Map<String, Object> tv = om.convertValue(
                    vars.get(i), new com.fasterxml.jackson.core.type.TypeReference<>() {
                    });
            String ta = answers.get(i).isTextual() ? answers.get(i).asText() : answers.get(i).toString();
            CheckRequest.DTO.TestSpecDTO t = new CheckRequest.DTO.TestSpecDTO(tv, ta);
            list.add(t);
        }
        return list;
    }
}
