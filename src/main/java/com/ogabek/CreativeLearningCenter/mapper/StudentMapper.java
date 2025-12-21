package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final PaymentRepository paymentRepository;

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .fullName(request.getFullName())
                .parentName(request.getParentName())
                .parentPhoneNumber(request.getParentPhoneNumber())
                .build();
    }

    public StudentResponse toResponse(Student student, BigDecimal totalPaid, List<StudentGroup> activeGroups) {
        LocalDate now = LocalDate.now();
        String currentMonth = now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        int groupsPaid = 0;
        int groupsUnpaid = 0;

        List<StudentResponse.GroupInfo> groupInfos = activeGroups.stream()
                .map(sg -> {
                    BigDecimal paidThisMonth = paymentRepository.getTotalPaidByStudentIdAndGroupIdAndMonth(
                            student.getId(), sg.getGroup().getId(), currentMonth);
                    
                    boolean hasPaid = paidThisMonth.compareTo(BigDecimal.ZERO) > 0;

                    return StudentResponse.GroupInfo.builder()
                            .groupId(sg.getGroup().getId())
                            .groupName(sg.getGroup().getName())
                            .teacherName(sg.getGroup().getTeacher().getFullName())
                            .monthlyFee(sg.getGroup().getMonthlyFee())
                            .paidForCurrentMonth(hasPaid)
                            .currentMonth(currentMonth)
                            .amountPaidThisMonth(paidThisMonth)
                            .build();
                })
                .toList();

        for (StudentResponse.GroupInfo groupInfo : groupInfos) {
            if (Boolean.TRUE.equals(groupInfo.getPaidForCurrentMonth())) {
                groupsPaid++;
            } else {
                groupsUnpaid++;
            }
        }

        boolean allPaid = groupsUnpaid == 0 && groupsPaid > 0;

        return StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .parentName(student.getParentName())
                .parentPhoneNumber(student.getParentPhoneNumber())
                .smsLinkCode(student.getSmsLinkCode())
                .totalPaid(totalPaid)
                .activeGroups(groupInfos)
                .activeGroupsCount(groupInfos.size())
                .paidForCurrentMonth(allPaid)
                .groupsPaidCount(groupsPaid)
                .groupsUnpaidCount(groupsUnpaid)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setFullName(request.getFullName());
        student.setParentName(request.getParentName());
        student.setParentPhoneNumber(request.getParentPhoneNumber());
    }
}
