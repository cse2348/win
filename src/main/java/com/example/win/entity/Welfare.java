package com.example.win.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Welfare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 정책명

    private String department; // 제공 부처

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadline; // 마감일

    @ElementCollection
    @CollectionTable(name = "welfare_keywords", joinColumns = @JoinColumn(name = "welfare_id"))
    @Column(name = "keyword")
    private List<String> keywords; // 키워드 태그

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    @Column(columnDefinition = "TEXT")
    private String eligibility; // 신청 대상 요약

    private String applyUrl; // 신청 링크 (옵션)
}