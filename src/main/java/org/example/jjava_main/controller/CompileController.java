package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.HttpUtil;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.dto.CompileRequest;
import org.example.jjava_main.dto.CompileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/compile")
public class CompileController {
    private final HttpUtil httpUtil;

    @PostMapping
    public ResponseEntity<?> compileProxy(@AuthenticationPrincipal User user, @RequestBody CompileRequest.DTO reqDTO) {
        // 프론트에서 받은 요청을 그대로 컴파일서버에 전달
        CompileResponse.DTO respDTO = httpUtil.compileServerSend(reqDTO, user.getId());
        return Resp.ok(respDTO);
    }

}
