package com.example.win.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalContent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary; // GPT로 요약된 내용

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false)
    private String originalLink;

    @Column(name = "representative_image_url", columnDefinition = "TEXT")
    private String representativeImageUrl; // DALL·E 이미지 URL

    // 감정 피드백 카운트 (추후 확장용)
    private int likedCount = 0;
    private int confusedCount = 0;
    private int sadCount = 0;
}
