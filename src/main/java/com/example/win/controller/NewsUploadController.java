package com.example.win.controller;

import com.example.win.dto.NewsCardDto;
import com.example.win.entity.News;
import com.example.win.service.ExcelNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsUploadController {

    private final ExcelNewsService newsService;

    /**
     * 최신 뉴스 목록을 카드 형태로 조회 (홈 화면용)
     */
    @GetMapping("/latest")
    public ResponseEntity<List<NewsCardDto>> getLatestNewsCards() {
        // 서비스에서 최신 뉴스 5개를 가져옴
        List<News> latestNews = newsService.getLatestNews(5);

        // DTO로 변환
        List<NewsCardDto> newsCards = latestNews.stream()
                .map(news -> NewsCardDto.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .summary(getFirstSentence(news.getSummary())) // 요약문 중 첫 문장만 추출
                        .representativeImageUrl("https://picsum.photos/300/200?random=" + news.getId()) // 임시 이미지 URL
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(newsCards);
    }

    // 요약문에서 첫 번째 문장만 깔끔하게 추출하는 헬퍼 메서드
    private String getFirstSentence(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int firstPeriod = text.indexOf('.');
        if (firstPeriod != -1) {
            return text.substring(0, firstPeriod + 1);
        }
        return text; // '.'이 없으면 전체 반환
    }
}