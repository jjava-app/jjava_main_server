package org.example.jjava_main._core.util;

import lombok.extern.slf4j.Slf4j;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class CompileClient {

    private final WebClient webClient;

    // 컴파일 서버 주소
    private static final String BASE_URL = "http://localhost:8081"; // 컴파일 서버 주소
    private static final String COMPILE_ENDPOINT = "/compile";

    public CompileClient() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    /**
     * 프론트에서 받은 코드를 컴파일 서버로 전송
     * <p>
     * reqDTO: 프론트에서 받은 요청(JSON)
     * return: 컴파일 서버 응답(JSON String)
     */
    public CompileResponse.DTO compileServerSend(CompileRequest.DTO reqDTO) {
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
                return new CompileResponse.DTO(null, resp.getMsg());
            }


            return resp.getBody();
        } catch (Exception e) {
            log.error("컴파일 서버 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("컴파일 서버와 통신 중 오류 발생");
        }
    }
}
