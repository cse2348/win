package com.example.win.repository;

import com.example.win.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // 최신순으로 뉴스를 조회하는 메서드
    List<News> findAllByOrderByPublicationDateDesc();

    // 제목과 날짜로 중복 여부 확인
    boolean existsByTitleAndPublicationDate(String title, LocalDate publicationDate);

    // 덮어쓰기용 - 기존 뉴스 1개 찾기
    News findByTitleAndPublicationDate(String title, LocalDate publicationDate);
}
