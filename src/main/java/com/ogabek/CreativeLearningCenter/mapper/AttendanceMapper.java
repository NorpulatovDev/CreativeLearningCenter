package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.response.AttendanceResponse;
import com.ogabek.CreativeLearningCenter.entity.Attendance;
import com.ogabek.CreativeLearningCenter.entity.AttendanceStatus;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AttendanceMapper {
    
    public Attendance toEntity(Student student, Group group, LocalDate date, AttendanceStatus status) {
        return Attendance.builder()
                .student(student)
                .group(group)
                .date(date)
                .status(status)
                .build();
    }
    
    public AttendanceResponse toResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .date(attendance.getDate())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFullName())
                .groupId(attendance.getGroup().getId())
                .groupName(attendance.getGroup().getName())
                .status(attendance.getStatus())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }
}
