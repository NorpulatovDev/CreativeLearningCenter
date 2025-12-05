package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateRequest {
    private String name;
    private Long teacherId;
    private BigDecimal monthlyFee;
}
