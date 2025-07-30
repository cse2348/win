package com.example.win.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiService openAiService() {
        // 배포 스크립트에서 주입해주는 환경 변수(OPENAI_API_KEY)를 직접 읽습니다.
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("OPENAI_API_KEY 환경 변수가 설정되지 않았습니다.");
        }
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
}