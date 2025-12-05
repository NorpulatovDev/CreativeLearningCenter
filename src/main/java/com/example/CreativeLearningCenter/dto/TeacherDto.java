package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private BigDecimal totalIncome;
}

