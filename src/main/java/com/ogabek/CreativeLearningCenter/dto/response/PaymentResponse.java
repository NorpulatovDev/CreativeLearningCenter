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
public class PaymentResponse {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private Long groupId;
    private String groupName;
    private BigDecimal amount;
    private String paidForMonth;
    private LocalDateTime paidAt;
}
