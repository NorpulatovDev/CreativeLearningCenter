package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.InquiryRequest;
import com.ogabek.CreativeLearningCenter.dto.response.InquiryResponse;
import com.ogabek.CreativeLearningCenter.entity.InquiryStatus;
import com.ogabek.CreativeLearningCenter.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@Tag(name = "Inquiries", description = "Qabulga kutayotgan o'quvchilar so'rovlari")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    @Operation(summary = "Yangi so'rov yaratish", description = "Qabulga kutayotgan o'quvchi so'rovini yaratish")
    public ResponseEntity<InquiryResponse> create(@Valid @RequestBody InquiryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "So'rovni ID bo'yicha olish")
    public ResponseEntity<InquiryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Barcha so'rovlarni olish")
    public ResponseEntity<List<InquiryResponse>> getAll() {
        return ResponseEntity.ok(inquiryService.getAll());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Status bo'yicha so'rovlarni olish", 
               description = "NEW, CONTACTED, ENROLLED, REJECTED")
    public ResponseEntity<List<InquiryResponse>> getByStatus(@PathVariable InquiryStatus status) {
        return ResponseEntity.ok(inquiryService.getByStatus(status));
    }

    @PutMapping("/{id}")
    @Operation(summary = "So'rovni yangilash")
    public ResponseEntity<InquiryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody InquiryRequest request) {
        return ResponseEntity.ok(inquiryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "So'rovni o'chirish")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inquiryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}