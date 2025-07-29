package com.example.win.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String index() {
        return "Spring Boot 애플리케이션이 성공적으로 실행되었습니다.";
    }

    // 헬스 체크용 엔드포인트 추가
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
