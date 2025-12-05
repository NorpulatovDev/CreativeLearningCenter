package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceHistoryFilterDto {
    private Long studentId;
    private Long groupId;
    private String month;  // Format: "2025-02"
}
