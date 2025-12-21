package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.TeacherRequest;
import com.ogabek.CreativeLearningCenter.dto.response.TeacherResponse;
import com.ogabek.CreativeLearningCenter.entity.Teacher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TeacherMapper {

    public Teacher toEntity(TeacherRequest request) {
        return Teacher.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    public TeacherResponse toResponse(Teacher teacher, BigDecimal totalIncome) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .fullName(teacher.getFullName())
                .phoneNumber(teacher.getPhoneNumber())
                .totalIncome(totalIncome)
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }

    public void updateEntity(Teacher teacher, TeacherRequest request) {
        teacher.setFullName(request.getFullName());
        teacher.setPhoneNumber(request.getPhoneNumber());
    }
}
