package com.example.win.controller;

import com.example.win.entity.GeneratedWelfare;
import com.example.win.service.GeneratedWelfareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/welfare/generate")
@RequiredArgsConstructor
public class GeneratedWelfareController {

    private final GeneratedWelfareService generatedWelfareService;

    @GetMapping
    public List<GeneratedWelfare> generateAndReturn() {
        return generatedWelfareService.generateAndSave();
    }
}
