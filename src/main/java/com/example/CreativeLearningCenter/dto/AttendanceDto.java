package com.example.CreativeLearningCenter.dto;

import com.example.CreativeLearningCenter.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {
    private Long id;
    private LocalDate date;
    private Long studentId;
    private String studentName;
    private Long groupId;
    private Attendance.AttendanceStatus status;
}

