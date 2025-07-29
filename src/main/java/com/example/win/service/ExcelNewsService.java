package com.example.win.service;

import com.example.win.entity.News;
import com.example.win.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ExcelNewsService {

    private final NewsRepository newsRepository;

    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 헤더 제외
                Row row = sheet.getRow(i);
                if (row == null) continue;

                News news = new News();
                news.setTitle(getCellValue(row.getCell(0)));
                news.setDate(getCellValue(row.getCell(1)));
                news.setLink(getCellValue(row.getCell(2)));
                news.setContent(getCellValue(row.getCell(3)));

                newsRepository.save(news);
            }
        } catch (IOException e) {
            throw new RuntimeException("엑셀 업로드 실패", e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }
}
