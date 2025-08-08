package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.compile.CheckService;
import org.example.jjava_main.domain.compile.CompileService;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CompileController {
    private final CompileService compileService;
    private final CheckService checkService;
    private final HttpUtil httpUtil;

    @PostMapping("/compile")
    public ResponseEntity<?> compileProxy(@RequestBody CompileRequest.DTO reqDTO) {
        // given
        Integer userId = 2;

        // 프론트에서 받은 요청을 그대로 컴파일서버에 전달
        CompileResponse.DTO respDTO = httpUtil.compileServerSend(reqDTO, userId);
        return Resp.ok(respDTO);
    }

}
