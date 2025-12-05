package com.example.CreativeLearningCenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotNull(message = "Monthly fee is required")
    @Positive(message = "Fee must be positive")
    private BigDecimal monthlyFee;
}
