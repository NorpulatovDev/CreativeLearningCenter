package com.example.CreativeLearningCenter.controller;

import com.example.CreativeLearningCenter.dto.PaymentDto;
import com.example.CreativeLearningCenter.dto.PaymentCreateRequest;
import com.example.CreativeLearningCenter.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByStudent(studentId));
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(paymentService.getPaymentsByGroup(groupId));
    }
    
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(paymentService.createPayment(request));
    }
}