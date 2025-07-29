package com.example.win.service;

import com.example.win.entity.Welfare;
import com.example.win.repository.WelfareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WelfareService {

    private final WelfareRepository welfareRepository;

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
    }
}