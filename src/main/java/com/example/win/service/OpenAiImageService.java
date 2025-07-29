package com.example.win.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiImageService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/images/generations")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    /**
     * GPT에서 받은 프롬프트를 바탕으로 DALL·E 이미지 URL을 생성 요청합니다.
     * @param prompt 이미지 생성용 프롬프트 (영어)
     * @return 생성된 이미지의 URL
     */
    public String generateImage(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "prompt", prompt,
                "n", 1,
                "size", "1024x1024",
                "response_format", "url"
        );

        try {
            return webClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(OpenAiImageResponse.class)
                    .map(response -> response.getData().get(0).getUrl())
                    .block();
        } catch (Exception e) {
            log.error("이미지 생성 실패: {}", e.getMessage());
            return "https://picsum.photos/300/200"; // fallback 이미지
        }
    }

    // 내부 응답 클래스
    public static class OpenAiImageResponse {
        private java.util.List<ImageData> data;

        public java.util.List<ImageData> getData() {
            return data;
        }

        public void setData(java.util.List<ImageData> data) {
            this.data = data;
        }
    }

    public static class ImageData {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
