package com.example.win.controller;

import com.example.win.dto.NewsCardDto;
import com.example.win.dto.NewsDetailDto;
import com.example.win.entity.News;
import com.example.win.service.ExcelNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<News> latestNews = newsService.getLatestNews(5); // 우선 5개만 조회

        List<NewsCardDto> newsCards = latestNews.stream()
                .map(news -> NewsCardDto.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .summary(news.getSummary().split("\\.")[0] + ".") // 요약문 중 첫 문장만 잘라서 제공
                        .representativeImageUrl("https://picsum.photos/200/300?random=" + news.getId()) // 임시 이미지 URL
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(newsCards);
    }

}