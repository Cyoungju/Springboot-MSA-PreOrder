package com.example.ecommerceproject.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<?> admin(){
        return ResponseEntity.ok(Map.of("msg", "관리자권한."));
    }
}
