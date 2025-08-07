package org.example.jjava_main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "<h1>Hello World!</h1>";
    }

    @GetMapping("/admin/health")
    public String adminHealth() {
        return "<h1>Hello World!</h1>";
    }
}
