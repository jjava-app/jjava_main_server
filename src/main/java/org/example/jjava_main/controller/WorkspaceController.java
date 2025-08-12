package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.workspace.Workspace;
import org.example.jjava_main.domain.workspace.WorkspaceService;
import org.example.jjava_main.dto.WorkspaceRequest;
import org.example.jjava_main.dto.WorkspaceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/workspace")
@RestController
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @GetMapping
    public ResponseEntity<?> workspaceList() {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        Integer userId = 1;
        List<Workspace> workspaceList = workspaceService.workspaceList(userId);
        return Resp.ok(workspaceList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> workspaceDetail(@PathVariable("id") Integer id) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        Integer userId = 1;
        Workspace workspace = workspaceService.workspaceDetail(id, userId);
        return Resp.ok(workspace);
    }

    @PostMapping
    public ResponseEntity<?> workspaceCreate() {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        Integer userId = 1;
        WorkspaceResponse.CreateDTO respDTO = workspaceService.workspaceCreate(userId);
        return Resp.ok(respDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> workspaceUpdate(@PathVariable("id") Integer id, @RequestBody WorkspaceRequest.UpdateDTO reqDTO) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        Integer userId = 1;
        WorkspaceResponse.DTO respDTO = workspaceService.workspaceUpdate(id, reqDTO, userId);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> workspaceDelete(@PathVariable("id") Integer id) {
        // TODO : given data -> AuthUtil에서 로그인한 유저 객체를 꺼내오게 해야 함 => JPA 연관 관계 설정 이슈
        Integer userId = 1;
        workspaceService.workspaceDelete(id, userId);
        return Resp.ok(null);
    }
}
