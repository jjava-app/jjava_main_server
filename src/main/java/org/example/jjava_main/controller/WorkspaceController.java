package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.workspace.Workspace;
import org.example.jjava_main.domain.workspace.WorkspaceService;
import org.example.jjava_main.dto.WorkspaceRequest;
import org.example.jjava_main.dto.WorkspaceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/workspace")
@RestController
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @GetMapping
    public ResponseEntity<?> workspaceList(@AuthenticationPrincipal User user) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        List<Workspace> workspaceList = workspaceService.workspaceList(user);
        return Resp.ok(workspaceList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> workspaceDetail(@PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        Workspace workspace = workspaceService.workspaceDetail(id, user);
        return Resp.ok(workspace);
    }

    @PostMapping
    public ResponseEntity<?> workspaceCreate(@AuthenticationPrincipal User user) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        WorkspaceResponse.CreateDTO respDTO = workspaceService.workspaceCreate(user);
        return Resp.ok(respDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> workspaceUpdate(@PathVariable("id") Integer id, @RequestBody WorkspaceRequest.UpdateDTO reqDTO, @AuthenticationPrincipal User user) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        WorkspaceResponse.DTO respDTO = workspaceService.workspaceUpdate(id, reqDTO, user);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> workspaceDelete(@PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        workspaceService.workspaceDelete(id, user);
        return Resp.ok(null);
    }
}
