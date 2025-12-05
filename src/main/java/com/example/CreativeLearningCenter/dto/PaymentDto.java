package com.example.CreativeLearningCenter.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long groupId;
    private String groupName;
    private BigDecimal amount;
    private String paidForMonth;
    private LocalDate paidAt;
}

