package com.akiyama.backend.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class TestController {

    @GetMapping("/public")
    public String publicApi() {
        System.out.println("public");
        return "PUBLIC OK";
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users")
    public String testUser() {
        System.out.println("users");
        return "users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String testADMIN() {
        System.out.println("admin");
        return "admin";
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/common")
    public String commonApi() {
        System.out.println("common");
        return "COMMON OK";
    }
    
}
