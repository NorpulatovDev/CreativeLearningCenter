package com.ogabek.CreativeLearningCenter.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRequest {
    
    @NotBlank(message = "Group name is required")
    private String name;
    
    @NotNull(message = "Teacher ID is required")
    private Long teacherId;
    
    @NotNull(message = "Monthly fee is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly fee must be greater than 0")
    private BigDecimal monthlyFee;
}
