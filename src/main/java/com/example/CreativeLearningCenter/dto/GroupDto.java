package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private BigDecimal monthlyFee;
    private LocalDateTime createdAt;
    private Long studentsCount;
    private BigDecimal totalAmountToPay;
    private BigDecimal totalPaid;
}

