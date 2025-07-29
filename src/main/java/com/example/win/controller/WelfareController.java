package com.example.win.controller;

import com.example.win.entity.Welfare;
import com.example.win.service.WelfareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/welfare")
public class WelfareController {

    private final WelfareService welfareService;

    // 등록
    @PostMapping
    public ResponseEntity<Welfare> saveWelfare(@RequestBody Welfare welfare) {
        System.out.println("📦 받은 데이터: " + welfare);
        return ResponseEntity.ok(welfareService.saveWelfare(welfare));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<Welfare>> getAllWelfare() {
        return ResponseEntity.ok(welfareService.getAllWelfare());
    }

    // 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<Welfare> getWelfareById(@PathVariable Long id) {
        return ResponseEntity.ok(welfareService.getWelfareById(id));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWelfare(@PathVariable Long id) {
        welfareService.deleteWelfare(id);
        return ResponseEntity.ok("삭제 완료");
    }
}