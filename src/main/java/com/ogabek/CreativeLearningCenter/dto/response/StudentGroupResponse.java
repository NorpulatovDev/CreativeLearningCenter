package com.ogabek.CreativeLearningCenter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGroupResponse {
    
    private Long id;
    private Long studentId;
    private String studentName;
    private Long groupId;
    private String groupName;
    private String teacherName;
    private BigDecimal monthlyFee;
    private Boolean active;
    private LocalDate enrolledAt;
    private LocalDate leftAt;
}
