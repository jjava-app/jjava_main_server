package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.user.admin.AdminService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AdminService adminService;

}
