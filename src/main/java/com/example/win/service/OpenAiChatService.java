package com.example.win.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenAiChatService {

    private final OpenAiService openAiService; // Config에서 Bean으로 등록한 Service를 주입받음

    private static final String SUMMARY_PROMPT =
            "다음 텍스트를 발달장애인이나 어린이가 이해하기 쉽도록 매우 쉬운 단어를 사용해서 키워드 3-4개와, 2-3문장으로 요약해줘. " +
                    "문장은 짧고 명확해야 하고, 존댓말로 부드럽게 설명해줘. " +
                    "결과물은 다른 설명 없이 요약된 문장만 깔끔하게 출력해줘. 텍스트: ";

    /**
     * 주어진 내용을 바탕으로 쉬운 문장 요약을 요청합니다.
     * @param content 원문 내용
     * @return GPT가 생성한 요약문
     */
    public String summarizeContent(String content) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Collections.singletonList(new ChatMessage("user", SUMMARY_PROMPT + content)))
                    .maxTokens(1000) // 한국어는 토큰을 많이 사용하므로 넉넉하게 설정
                    .temperature(0.5)
                    .build();

            return openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            System.err.println("GPT API 호출 중 오류 발생: " + e.getMessage());
            return content.substring(0, Math.min(content.length(), 150)) + "... (요약 중 오류 발생)";
        }
    }

    /**
     * 뉴스 제목과 요약문을 바탕으로 이미지 생성을 위한 프롬프트를 요청합니다.
     * @param title 뉴스 제목
     * @param summary 요약된 뉴스 내용
     * @return GPT가 생성한 이미지 프롬프트 (영어, 장면 묘사 중심)
     */
    public String generateImagePrompt(String title, String summary) {
        if (title == null) title = "";
        if (summary == null) summary = "";

        String imagePromptRequest =
                "뉴스 제목: " + title + "\n" +
                        "요약: " + summary + "\n\n" +
                        "위 뉴스를 바탕으로 현실감 있는 대표 이미지를 생성할 수 있도록 영어로 프롬프트를 만들어줘. " +
                        "프롬프트에는 장소, 등장 인물, 배경, 분위기, 상황이 포함되어야 해. " +
                        "형식은 하나의 영어 문장으로 만들어줘. 다른 설명 없이 프롬프트 문장만 출력해.";

        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4o")
                    .messages(Collections.singletonList(new ChatMessage("user", imagePromptRequest)))
                    .maxTokens(300)
                    .temperature(0.7)
                    .build();

            return openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            System.err.println("GPT 이미지 프롬프트 생성 중 오류 발생: " + e.getMessage());
            return "A symbolic image representing the news topic with a realistic atmosphere.";
        }
    }
}
