package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.workspace.WorkspaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/workspace")
@RestController
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @GetMapping
    public ResponseEntity<?> workspaceList() {
        workspaceService.workspaceList();
        return Resp.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> workspaceDetail(@PathVariable("id") Integer id) {
        workspaceService.workspaceDetail();
        return Resp.ok(null);
    }

    @PostMapping
    public ResponseEntity<?> workspaceCreate() {
        String resp = workspaceService.workspaceCreate();
        return Resp.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> workspaceUpdate(@PathVariable("id") Integer id) {
        workspaceService.workspaceUpdate();
        return Resp.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> workspaceDelete(@PathVariable("id") Integer id) {
        workspaceService.workspaceDelete();
        return Resp.ok(null);
    }
}
