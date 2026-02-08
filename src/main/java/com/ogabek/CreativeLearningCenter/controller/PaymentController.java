package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.PaymentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.PaymentResponse;
import com.ogabek.CreativeLearningCenter.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentResponse>> getByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(paymentService.getByStudentId(studentId));
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PaymentResponse>> getByGroupId(@PathVariable Long groupId) {
        return ResponseEntity.ok(paymentService.getByGroupId(groupId));
    }
    
    @GetMapping("/group/{groupId}/month/{year}/{month}")
    @Operation(summary = "Guruhga qilingan to'lovlarni oy bo'yicha olish",
               description = "Ma'lum bir guruh uchun ma'lum bir oydagi barcha to'lovlarni qaytaradi")
    public ResponseEntity<List<PaymentResponse>> getByGroupIdAndMonth(
            @PathVariable Long groupId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(paymentService.getByGroupIdAndMonth(groupId, year, month));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}