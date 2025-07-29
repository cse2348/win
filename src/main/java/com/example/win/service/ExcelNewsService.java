package com.example.win.service;

import com.example.win.entity.News;
import com.example.win.repository.NewsRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelNewsService {

    private final NewsRepository newsRepository;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String SUMMARY_PROMPT =
            "다음 텍스트를 발달장애인이나 어린이가 이해하기 쉽도록 매우 쉬운 단어를 사용해서 3-4문장으로 요약해줘. " +
                    "문장은 짧고 명확해야 하고, 존댓말로 부드럽게 설명해줘. " +
                    "결과물은 요약된 문장만 포함해줘. 텍스트: ";

    /**
     * 리소스 폴더에 있는 엑셀 파일을 읽어 DB에 저장합니다.
     * @param filePath 리소스 폴더 내의 파일 경로 (예: "data/news.xlsx")
     */
    @Transactional
    public void loadExcelDataToDb(String filePath) {
        // 이미 데이터가 있다면 실행하지 않음 (중복 방지)
        if (newsRepository.count() > 0) {
            System.out.println("데이터가 이미 존재합니다. 엑셀 로딩을 건너뜁니다.");
            return;
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행(헤더)은 건너뜀
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String title = row.getCell(0).getStringCellValue();
                String dateStr = row.getCell(1).getStringCellValue();
                String link = row.getCell(2).getStringCellValue();
                String content = row.getCell(3).getStringCellValue();

                // 1. ChatGPT로 내용 요약
                String summary = summarizeContentWithGpt(content);

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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * OpenAI GPT를 사용하여 뉴스 내용을 요약합니다.
     * @param content 원문 내용
     * @return 요약된 내용
     */
    private String summarizeContentWithGpt(String content) {
        try {
            OpenAiService service = new OpenAiService(openaiApiKey, Duration.ofSeconds(30));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Collections.singletonList(new ChatMessage("user", SUMMARY_PROMPT + content)))
                    .maxTokens(500) // 요약문의 최대 길이를 넉넉하게 설정
                    .temperature(0.5) // 창의성 조절 (낮을수록 결정적)
                    .build();

            return service.createChatCompletion(request).getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            e.printStackTrace();
            // API 호출 실패 시, 원문의 앞부분을 잘라서 임시 요약문으로 사용
            return content.substring(0, Math.min(content.length(), 150)) + "...";
        }
    }

    // --- API 제공용 메서드 ---

    @Transactional(readOnly = true)
    public List<News> getLatestNews(int count) {
        List<News> allNews = newsRepository.findAllByOrderByPublicationDateDesc();
        return allNews.subList(0, Math.min(count, allNews.size()));
    }

    @Transactional(readOnly = true)
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 뉴스를 찾을 수 없습니다: " + id));
    }
}