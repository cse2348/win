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

    // 복지 등록
    @PostMapping
    public ResponseEntity<Welfare> saveWelfare(@RequestBody Welfare welfare) {
        System.out.println("받은 데이터: " + welfare);
        return ResponseEntity.ok(welfareService.saveWelfare(welfare));
    }

    // 복지 전체 조회
    @GetMapping
    public ResponseEntity<List<Welfare>> getAllWelfare() {
        return ResponseEntity.ok(welfareService.getAllWelfare());
    }

    // 복지 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<Welfare> getWelfareById(@PathVariable Long id) {
        return ResponseEntity.ok(welfareService.getWelfareById(id));
    }

    // 복지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWelfare(@PathVariable Long id) {
        welfareService.deleteWelfare(id);
        return ResponseEntity.ok("삭제 완료");
    }

    // 복지 북마크 추가
    @PostMapping("/{welfareId}/bookmark")
    public ResponseEntity<String> addBookmark(@PathVariable Long welfareId) {
        welfareService.addBookmark(welfareId); // 실제 구현에서는 사용자 정보도 필요할 수 있음
        return ResponseEntity.ok("북마크 추가 완료");
    }

    // 복지 북마크 해제
    @DeleteMapping("/{welfareId}/bookmark")
    public ResponseEntity<String> removeBookmark(@PathVariable Long welfareId) {
        welfareService.removeBookmark(welfareId);
        return ResponseEntity.ok("북마크 해제 완료");
    }

    //  내 복지 북마크 조회
    @GetMapping("/me/bookmark")
    public ResponseEntity<List<Welfare>> getMyBookmarks() {
        return ResponseEntity.ok(welfareService.getMyBookmarks());
    }
}