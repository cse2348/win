package com.example.win.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsCardDto {
    private Long id;
    private String title;
    private String summary; // 요약된 한 문장
    private String representativeImageUrl; // 대표 이미지 URL (지금은 임시값)
}