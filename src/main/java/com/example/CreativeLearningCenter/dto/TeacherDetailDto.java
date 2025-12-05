package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDetailDto {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private List<GroupSummaryDto> groups;
    private BigDecimal totalIncome;
}
