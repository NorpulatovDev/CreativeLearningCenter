package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.SmsLinkResponse;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;
import com.ogabek.CreativeLearningCenter.entity.Student;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StudentMapper {
    
    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .fullName(request.getFullName())
                .parentName(request.getParentName())
                .parentPhoneNumber(request.getParentPhoneNumber())
                .build();
    }
    
    public StudentResponse toResponse(Student student, BigDecimal totalPaid) {
        return StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .parentName(student.getParentName())
                .parentPhoneNumber(student.getParentPhoneNumber())
                .smsLinked(student.getSmsLinked())
                .smsLinkCode(student.getSmsLinkCode())
                .totalPaid(totalPaid)
                .activeGroupId(student.getActiveGroup() != null ? student.getActiveGroup().getId() : null)
                .activeGroupName(student.getActiveGroup() != null ? student.getActiveGroup().getName() : null)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }
    
    public SmsLinkResponse toSmsLinkResponse(Student student) {
        return SmsLinkResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .smsLinkCode(student.getSmsLinkCode())
                .smsLinked(student.getSmsLinked())
                .parentPhoneNumber(student.getParentPhoneNumber())
                .build();
    }
    
    public void updateEntity(Student student, StudentRequest request) {
        student.setFullName(request.getFullName());
        student.setParentName(request.getParentName());
        student.setParentPhoneNumber(request.getParentPhoneNumber());
    }
}