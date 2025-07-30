package com.example.win.dto;

import com.example.win.entity.News;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class NewsDetailDto {
    private Long id;
    private String title;
    private String keywords;
    private String summary;
    private LocalDate publicationDate;
    private String originalLink;

    public static NewsDetailDto fromEntity(News news) {
        return NewsDetailDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .keywords(news.getKeywords())
                .summary(news.getSummary())
                .publicationDate(news.getPublicationDate())
                .originalLink(news.getOriginalLink())
                .build();
    }
}