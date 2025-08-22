package org.example.jjava_main._core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main._core.error.ex.Exception404;
import org.example.jjava_main.domain.question.Question;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.example.jjava_main.dto.CheckRequest;
import org.example.jjava_main.dto.CheckResponse;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
public class HttpUtil {

    private final RestTemplate restTemplate;

    private final QuestionRepository questionRepository;

    //private static final String BASE_URL = "http://jjava-compile:8081";
    private static final String BASE_URL = "http://localhost:8081";
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CompileRequest.DTO> requestEntity = new HttpEntity<>(reqDTO, headers);

            ResponseEntity<Resp<CompileResponse.DTO>> responseEntity =
                    restTemplate.exchange(
                            BASE_URL + COMPILE_ENDPOINT,
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            Resp<CompileResponse.DTO> resp = responseEntity.getBody();

            if (resp == null) {
                throw new RuntimeException("컴파일 서버 응답이 없습니다.");
            }

            if (resp.getStatus() != 200) {
                // 실패 응답이지만 통신은 정상 → 프론트로 그대로 전달
                return new CompileResponse.DTO(userId, null, resp.getMsg());
            }

            // 컴파일 서버 응답에 userId를 추가
            CompileResponse.DTO respDTO = resp.getBody();
            if (respDTO == null) {
                throw new RuntimeException("컴파일 서버 응답 body가 비어 있습니다.");
            }
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

            // 문제 조회
            Question q = questionRepository.findById(questionId).orElse(null);
            if (q == null) throw new Exception404("문제를 찾을 수 없습니다.");

            // 테스트 케이스 구성
            List<CheckRequest.DTO.TestSpecDTO> tests = buildTestsFromQuestion(q, om);
            CheckRequest.DTO outbound = new CheckRequest.DTO(reqDTO.getPayload(), tests);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CheckRequest.DTO> requestEntity = new HttpEntity<>(outbound, headers);

            ResponseEntity<Resp<JsonNode>> responseEntity =
                    restTemplate.exchange(
                            BASE_URL + CHECK_ENDPOINT,
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            Resp<JsonNode> resp = responseEntity.getBody();

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
                return List.of(f);
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
            throw new Exception404("test_variable / test_answer는 배열이어야 합니다.");
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
