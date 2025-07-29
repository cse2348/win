package com.example.win.repository;

import com.example.win.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    // 최신순으로 뉴스를 조회하는 메서드
    List<News> findAllByOrderByPublicationDateDesc();
}