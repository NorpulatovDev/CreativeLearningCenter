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
    private BigDecimal totalAmountToPay;
    private BigDecimal totalPaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
