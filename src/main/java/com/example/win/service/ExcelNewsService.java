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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelNewsService {

    private final NewsRepository newsRepository;
    private final OpenAiChatService openAiChatService; // 분리된 OpenAI 서비스 주입

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

                System.out.println((i) + "번째 뉴스 요약 시작: " + title);
                // 1. ChatGPT로 내용 요약 (분리된 서비스 호출)
                String summary = openAiChatService.summarizeContent(content);

                // 2. News 엔티티 생성
                News news = News.builder()
                        .title(title)
                        .publicationDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                        .originalLink(link)
                        .originalContent(content)
                        .summary(summary)
                        .build();

                // 3. DB에 저장
                newsRepository.save(news);
            }
            System.out.println("뉴스 데이터 로딩 및 요약 완료!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 처리 중 심각한 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<News> getLatestNews(int count) {
        List<News> allNews = newsRepository.findAllByOrderByPublicationDateDesc();
        return allNews.subList(0, Math.min(count, allNews.size()));
    }
}