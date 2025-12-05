package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailDto {
    private Long id;
    private String name;
    private TeacherDto teacher;
    private BigDecimal monthlyFee;
    private LocalDateTime createdAt;
    private List<StudentDto> students;
    private Long studentsCount;
    private BigDecimal totalAmountToPay;
    private BigDecimal totalPaid;
}
