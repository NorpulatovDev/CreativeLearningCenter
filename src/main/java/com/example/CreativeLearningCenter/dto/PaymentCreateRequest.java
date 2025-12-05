package com.example.CreativeLearningCenter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Payment DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Paid for month is required")
    private String paidForMonth;  // Format: "2025-02"
}
