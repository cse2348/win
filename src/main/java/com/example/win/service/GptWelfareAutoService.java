package com.example.win.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptWelfareAutoService {

    private final OpenAiService openAiService;

    public String complete(String prompt) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Collections.singletonList(new ChatMessage("user", prompt)))
                    .maxTokens(1500)
                    .temperature(0.7)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            List<com.theokanning.openai.completion.chat.ChatCompletionChoice> choices = result.getChoices();

            if (choices == null || choices.isEmpty()) {
                System.err.println("GPT 응답 실패: 결과가 없습니다.");
                return "[]";
            }

            return choices.get(0).getMessage().getContent().trim();

        } catch (Exception e) {
            System.err.println("GPT 호출 중 오류 발생: " + e.getMessage());
            return "[]";
        }
    }
}
