package com.ogabek.CreativeLearningCenter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {
    
    private Long id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private BigDecimal monthlyFee;
    private Integer studentsCount;
    private BigDecimal totalAmountToPay; // studentsCount * monthlyFee
    private BigDecimal totalPaid; // sum of all payments for this group
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}