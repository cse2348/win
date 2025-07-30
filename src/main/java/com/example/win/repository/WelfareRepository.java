package com.example.win.repository;

import com.example.win.entity.Welfare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WelfareRepository extends JpaRepository<Welfare, Long> {
    // 필요한 경우 커스텀 쿼리 작성 가능
}