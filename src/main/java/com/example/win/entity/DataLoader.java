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
        excelNewsService.loadExcelDataToDb("data/politics.xlsx");
    }
}