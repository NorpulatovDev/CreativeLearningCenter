package com.example.CreativeLearningCenter.dto;

import com.example.CreativeLearningCenter.entity.Attendance;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Attendance DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMarkRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Status is required")
    private Attendance.AttendanceStatus status;
}
