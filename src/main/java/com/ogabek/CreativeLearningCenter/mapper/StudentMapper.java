package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.SmsLinkResponse;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .fullName(request.getFullName())
                .parentName(request.getParentName())
                .parentPhoneNumber(request.getParentPhoneNumber())
                .build();
    }

    public StudentResponse toResponse(Student student, BigDecimal totalPaid, List<StudentGroup> activeGroups) {
        List<StudentResponse.GroupInfo> groupInfos = activeGroups.stream()
                .map(sg -> StudentResponse.GroupInfo.builder()
                        .groupId(sg.getGroup().getId())
                        .groupName(sg.getGroup().getName())
                        .teacherName(sg.getGroup().getTeacher().getFullName())
                        .monthlyFee(sg.getGroup().getMonthlyFee())
                        .build())
                .toList();

        return StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .parentName(student.getParentName())
                .parentPhoneNumber(student.getParentPhoneNumber())
                .smsLinked(student.getSmsLinked())
                .smsLinkCode(student.getSmsLinkCode())
                .totalPaid(totalPaid)
                .activeGroups(groupInfos)
                .activeGroupsCount(groupInfos.size())
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