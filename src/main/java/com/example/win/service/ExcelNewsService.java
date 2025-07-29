package com.example.win.service;

import com.example.win.entity.News;
import com.example.win.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
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

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd. a h:mm", Locale.KOREAN);

    @Transactional
    public void loadExcelDataToDb(String filePath) {
        String category = extractCategoryFromFileName(filePath);
        System.out.println(filePath + "에서 [" + category + "] 뉴스 데이터를 로딩합니다...");

        try (InputStream is = getExcelFileAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) continue;

                String title = row.getCell(0).getStringCellValue().trim();
                String dateStr = row.getCell(1).getStringCellValue().trim();
                String link = row.getCell(2).getStringCellValue().trim();
                String content = row.getCell(3).getStringCellValue().trim();

                String imageUrl;
                try {
                    imageUrl = row.getCell(4) != null ? row.getCell(4).getStringCellValue().trim() : "";
                    if (imageUrl.isEmpty() || imageUrl.contains("이미지 없음")) {
                        imageUrl = "https://picsum.photos/300/200?random=" + i;
                    }
                } catch (Exception e) {
                    imageUrl = "https://picsum.photos/300/200?random=" + i;
                }

                LocalDate date;
                try {
                    LocalDateTime dt = LocalDateTime.parse(dateStr, formatter);
                    date = dt.toLocalDate();
                } catch (DateTimeParseException e) {
                    System.out.println("날짜 파싱 실패: " + dateStr);
                    continue;
                }

                if (newsRepository.existsByTitleAndPublicationDate(title, date)) {
                    System.out.println("[" + title + "] 이미 존재함 → 덮어쓰기");
                    News existing = newsRepository.findByTitleAndPublicationDate(title, date);
                    newsRepository.delete(existing);
                }

                String summaryRaw;
                String summaryText = "";
                String keywords = "";

                try {
                    System.out.println(i + "번째 뉴스 요약 시작: " + title);
                    summaryRaw = openAiChatService.summarizeContent(content, "매우 쉬움");
                    System.out.println("GPT 응답:\n" + summaryRaw);

                    for (String line : summaryRaw.split("\n")) {
                        line = line.trim();

                        // 키워드 파싱
                        if (line.toLowerCase().contains("키워드") || line.matches("^#?\\w+(\\s+#?\\w+)*$")) {
                            keywords = line.replaceAll("(?i)\\[?키워드\\]?[:：]?", "").trim();
                        }

                        // 요약 파싱
                        else if (line.toLowerCase().contains("요약")) {
                            summaryText = line.replaceAll("(?i)\\[?요약\\]?[:：]?", "").trim();
                        }
                    }

                    // 혹시 [요약]이 붙은 한 줄 응답 처리
                    if ((keywords.isEmpty() || summaryText.isEmpty()) && summaryRaw.contains("[") && summaryRaw.contains("#")) {
                        String[] parts = summaryRaw.split("\\[요약\\]");
                        if (parts.length == 2) {
                            keywords = parts[0].replace("[키워드]", "").trim();
                            summaryText = parts[1].trim();
                        }
                    }

                    if (summaryText.isEmpty()) summaryText = "요약 실패";
                    if (keywords.isEmpty()) keywords = "#요약실패";

                } catch (Exception e) {
                    System.out.println("GPT 요약 실패: " + e.getMessage());
                    summaryText = "요약 실패";
                    keywords = "#요약실패";
                }

                News news = News.builder()
                        .title(title)
                        .publicationDate(date)
                        .originalLink(link)
                        .originalContent(content)
                        .summary(summaryText)
                        .keywords(keywords)
                        .representativeImageUrl(imageUrl)
                        .category(category)
                        .build();

                newsRepository.save(news);
            }

            System.out.println("[" + category + "] 뉴스 데이터 로딩 및 요약 완료!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    private InputStream getExcelFileAsStream(String filePath) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(filePath);
        if (stream == null) {
            throw new RuntimeException("엑셀 파일을 찾을 수 없습니다: " + filePath);
        }
        return stream;
    }

    @Transactional
    public void save(News news) {
        newsRepository.save(news);
    }

    @Transactional(readOnly = true)
    public List<News> getLatestNews(int count) {
        List<News> allNews = newsRepository.findAllByOrderByPublicationDateDesc();
        return allNews.subList(0, Math.min(count, allNews.size()));
    }

    private String extractCategoryFromFileName(String filePath) {
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1).toLowerCase();
        if (filename.contains("politic")) return "정치";
        if (filename.contains("economy")) return "경제";
        if (filename.contains("society")) return "사회";
        if (filename.contains("culture")) return "문화";
        return "기타";
    }
}
