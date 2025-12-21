package com.ogabek.CreativeLearningCenter.dto.response;

import com.ogabek.CreativeLearningCenter.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    
    private Long id;
    private LocalDate date;
    private Long studentId;
    private String studentName;
    private Long groupId;
    private String groupName;
    private AttendanceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
