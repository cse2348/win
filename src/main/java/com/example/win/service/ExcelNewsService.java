package com.example.win.service;

import com.example.win.entity.News;
import com.example.win.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ExcelNewsService {

    private final NewsRepository newsRepository;
    private final OpenAiChatService openAiChatService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. a h:mm", Locale.KOREAN);

    @Transactional
    public void loadExcelDataToDb(String filePath) {
        if (newsRepository.count() > 0) {
            System.out.println("데이터가 이미 존재하므로 엑셀 로딩을 건너뜁니다.");
            return;
        }

        System.out.println(filePath + "에서 뉴스 데이터를 로딩합니다...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) continue;

                String title = row.getCell(0).getStringCellValue();
                String dateStr = row.getCell(1).getStringCellValue();
                String link = row.getCell(2).getStringCellValue();
                String content = row.getCell(3).getStringCellValue();

                // 이미지 URL (5번째 열)
                String imageUrl = null;
                try {
                    imageUrl = row.getCell(4) != null ? row.getCell(4).getStringCellValue().trim() : "";
                    if (imageUrl.isEmpty()) {
                        imageUrl = "https://picsum.photos/300/200?random=" + i;
                    }
                } catch (Exception e) {
                    imageUrl = "https://picsum.photos/300/200?random=" + i;
                }

                // 날짜 파싱
                LocalDate date;
                try {
                    LocalDateTime dt = LocalDateTime.parse(dateStr, formatter);
                    date = dt.toLocalDate();
                } catch (DateTimeParseException e) {
                    System.out.println("날짜 파싱 실패: " + dateStr);
                    continue;
                }

                // GPT 요약
                String summary;
                try {
                    System.out.println(i + "번째 뉴스 요약 시작: " + title);
                    summary = openAiChatService.summarizeContent(content);
                } catch (Exception e) {
                    System.out.println("GPT 요약 실패: " + e.getMessage());
                    summary = "요약 실패";
                }

                News news = News.builder()
                        .title(title)
                        .publicationDate(date)
                        .originalLink(link)
                        .originalContent(content)
                        .summary(summary)
                        .representativeImageUrl(imageUrl)
                        .build();

                newsRepository.save(news);
            }

            System.out.println("뉴스 데이터 로딩 및 요약 완료!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    public void save(News news) {
        newsRepository.save(news);
    }

    @Transactional(readOnly = true)
    public List<News> getLatestNews(int count) {
        List<News> allNews = newsRepository.findAllByOrderByPublicationDateDesc();
        return allNews.subList(0, Math.min(count, allNews.size()));
    }
}
