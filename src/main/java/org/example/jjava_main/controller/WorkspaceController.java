package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.workspace.WorkspaceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/workspace")
@RestController
public class WorkspaceController {
    private final WorkspaceService workspaceService;

}
