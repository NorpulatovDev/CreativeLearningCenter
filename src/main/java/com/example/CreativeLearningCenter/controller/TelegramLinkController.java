package com.example.CreativeLearningCenter.controller;

import com.example.CreativeLearningCenter.dto.TelegramLinkByPhoneRequest;
import com.example.CreativeLearningCenter.dto.TelegramLinkByCodeRequest;
import com.example.CreativeLearningCenter.dto.TelegramLinkResponseDto;
import com.example.CreativeLearningCenter.service.TelegramLinkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramLinkController {
    
    private final TelegramLinkingService telegramLinkingService;
    
    @PostMapping("/link/by-phone")
    public ResponseEntity<TelegramLinkResponseDto> linkByPhone(
            @Valid @RequestBody TelegramLinkByPhoneRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(telegramLinkingService.linkByPhone(request));
    }
    
    @PostMapping("/link/by-code")
    public ResponseEntity<TelegramLinkResponseDto> linkByCode(
            @Valid @RequestBody TelegramLinkByCodeRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(telegramLinkingService.linkByCode(request));
    }
    
    @GetMapping("/link/{studentId}")
    public ResponseEntity<TelegramLinkResponseDto> getLinkInfo(@PathVariable Long studentId) {
        return ResponseEntity.ok(telegramLinkingService.getLinkInfo(studentId));
    }
}