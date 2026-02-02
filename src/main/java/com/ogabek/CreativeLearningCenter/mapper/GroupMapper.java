package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.GroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.GroupResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Teacher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GroupMapper {
    
    public Group toEntity(GroupRequest request, Teacher teacher) {
        return Group.builder()
                .name(request.getName())
                .teacher(teacher)
                .monthlyFee(request.getMonthlyFee())
                .build();
    }

    public GroupResponse toResponse(Group group, int activeStudentsCount, BigDecimal totalPaid) {
        // Monthly expected = monthlyFee × number of students
        BigDecimal totalAmountToPay = group.getMonthlyFee()
                .multiply(BigDecimal.valueOf(activeStudentsCount));

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .teacherId(group.getTeacher().getId())
                .teacherName(group.getTeacher().getFullName())
                .monthlyFee(group.getMonthlyFee())
                .studentsCount(activeStudentsCount)
                .totalAmountToPay(totalAmountToPay)  // Monthly expected
                .totalPaid(totalPaid)  // Monthly paid
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
    
    public void updateEntity(Group group, GroupRequest request, Teacher teacher) {
        group.setName(request.getName());
        group.setTeacher(teacher);
        group.setMonthlyFee(request.getMonthlyFee());
    }
}
