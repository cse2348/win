package com.example.win.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenAiChatService {

    private final OpenAiService openAiService;

    public String summarizeContent(String content, String level) {
        String prompt = buildPromptByLevel(level, content);

        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Collections.singletonList(new ChatMessage("user", prompt)))
                    .maxTokens(1000)
                    .temperature(0.5)
                    .build();

            return openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent().trim();

        } catch (Exception e) {
            System.err.println("GPT API 호출 중 오류 발생: " + e.getMessage());
            return content.substring(0, Math.min(content.length(), 150)) + "... (요약 중 오류 발생)";
        }
    }

    private String buildPromptByLevel(String level, String content) {
        String difficultyInstruction = switch (level) {
            case "매우 쉬움" -> """
                다음 글을 5~6세 어린이나 지적 발달장애인이 이해할 수 있도록 아주 쉬운 단어로 설명해줘.
                - 문장은 아주 짧고, 1~2문장만 써줘.
                - 어려운 개념은 빼거나 아주 간단하게 바꿔줘.
                - 존댓말로 친절하게 말해줘.
                """;
            case "쉬움" -> """
                다음 글을 10~13세 어린이 또는 경증 발달장애인이 이해할 수 있도록 쉽게 설명해줘.
                - 핵심 정보는 유지하면서 쉬운 단어로 표현해줘.
                - 문장은 2~3문장 정도로 써줘.
                - 존댓말로, 부드럽고 친절하게 써줘.
                """;
            default -> """
                다음 글을 발달장애인이나 어린이가 이해하기 쉽도록 쉬운 단어를 사용해서 설명해줘.
                - 문장은 2~3문장으로 짧게 써줘.
                - 존댓말로, 부드럽게 말해줘.
                """;
        };

        return String.join("\n",
                difficultyInstruction,
                "- 키워드는 꼭 3~4개만 골라줘.",
                "- 아래 형식으로 출력해줘:",
                "[키워드] #키워드1 #키워드2 #키워드3",
                "[요약] 문장",
                "- 다른 설명은 절대 하지 마. 위 형식만 보여줘.",
                "",
                "텍스트: " + content
        );
    }
}
