package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupSummaryDto {
    private Long id;
    private String name;
    private BigDecimal monthlyFee;
}
