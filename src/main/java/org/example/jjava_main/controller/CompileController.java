package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.compile.CheckService;
import org.example.jjava_main.domain.compile.CompileService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CompileController {
    private final CompileService compileService;
    private final CheckService checkService;

}
