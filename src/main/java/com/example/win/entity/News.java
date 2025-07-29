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
    private String summary;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false)
    private String originalLink;

    @Column(name = "representative_image_url", columnDefinition = "TEXT")
    private String representativeImageUrl;

    @Column(nullable = false)
    private String category;

    @Builder.Default
    private int likedCount = 0;

    @Builder.Default
    private int confusedCount = 0;

    @Builder.Default
    private int sadCount = 0;

}
