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
public class TeacherResponse {
    
    private Long id;
    private String fullName;
    private String phoneNumber;
    private BigDecimal totalIncome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
