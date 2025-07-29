package com.example.win.controller;

import com.example.win.dto.NewsCardDto;
import com.example.win.entity.News;
import com.example.win.service.ExcelNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
        List<News> latestNews = newsService.getLatestNews(5);
        List<NewsCardDto> newsCards = latestNews.stream()
                .map(news -> NewsCardDto.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .keywords(news.getKeywords())
                        .summary(getFirstSentence(news.getSummary()))
                        .representativeImageUrl(news.getRepresentativeImageUrl()) // DB에 저장된 URL 사용
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(newsCards);
    }

    private String getFirstSentence(String text) {
        if (text == null || text.isEmpty()) return "";
        int firstPeriod = text.indexOf('.');
        return firstPeriod != -1 ? text.substring(0, firstPeriod + 1) : text;
    }

    /**
     * 테스트용 단일 뉴스 더미 저장
     */
    @GetMapping("/insert-dummy")
    public ResponseEntity<String> insertDummyNews() {
        News dummy = News.builder()
                .title("테스트 뉴스입니다")
                .summary("이것은 요약 테스트 문장입니다. 추가 내용 생략.")
                .originalContent("이것은 원문 내용입니다.")
                .originalLink("https://example.com")
                .publicationDate(LocalDate.now())
                .representativeImageUrl("https://picsum.photos/300/200?random=dummy")
                .category("테스트")
                .build();

        newsService.save(dummy);
        return ResponseEntity.ok("더미 뉴스 저장 완료!");
    }

    /**
     * 엑셀에서 뉴스 일괄 로딩
     */
    @GetMapping("/load-from-excel")
    public ResponseEntity<String> insertNewsFromExcel(@RequestParam String filename) {
        try {
            String path = "data/" + filename;
            newsService.loadExcelDataToDb(path);
            return ResponseEntity.ok("엑셀에서 뉴스 데이터 로딩 성공: " + filename);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("엑셀 로딩 실패: " + e.getMessage());
        }
    }
}
