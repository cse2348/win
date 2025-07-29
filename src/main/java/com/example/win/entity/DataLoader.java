package com.example.win.entity;

import com.example.win.service.ExcelNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ExcelNewsService excelNewsService;

    @Override
    public void run(String... args) throws Exception {
        // resources 폴더 아래에 data 폴더를 만들고 news.xlsx 파일을 넣어주세요.
        excelNewsService.loadExcelDataToDb("data/news.xlsx");
    }
}