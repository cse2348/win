package com.example.win.service;

import com.example.win.entity.Welfare;
import com.example.win.repository.WelfareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WelfareService {

    private final WelfareRepository welfareRepository;

    // 북마크 목록 (메모리 기반 임시 저장)
    private final Set<Long> bookmarkedWelfareIds = new HashSet<>();

    // 복지정보 등록
    public Welfare saveWelfare(Welfare welfare) {
        return welfareRepository.save(welfare);
    }

    // 복지정보 전체 조회
    public List<Welfare> getAllWelfare() {
        return welfareRepository.findAll();
    }

    // 특정 ID로 조회
    public Welfare getWelfareById(Long id) {
        return welfareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 복지 정보를 찾을 수 없습니다."));
    }

    // 삭제
    public void deleteWelfare(Long id) {
        welfareRepository.deleteById(id);
        bookmarkedWelfareIds.remove(id); // 삭제 시 북마크에서도 제거
    }

    // 북마크 추가
    public void addBookmark(Long welfareId) {
        if (!welfareRepository.existsById(welfareId)) {
            throw new IllegalArgumentException("해당 복지 ID가 존재하지 않습니다.");
        }
        bookmarkedWelfareIds.add(welfareId);
    }

    // 북마크 해제
    public void removeBookmark(Long welfareId) {
        bookmarkedWelfareIds.remove(welfareId);
    }

    // 내 북마크 목록 조회
    public List<Welfare> getMyBookmarks() {
        return welfareRepository.findAllById(bookmarkedWelfareIds);
    }
}