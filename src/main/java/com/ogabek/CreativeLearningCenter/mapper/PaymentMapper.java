package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.PaymentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.PaymentResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Payment;
import com.ogabek.CreativeLearningCenter.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public Payment toEntity(PaymentRequest request, Student student, Group group) {
        return Payment.builder()
                .student(student)
                .group(group)
                .amount(request.getAmount())
                .paidForMonth(request.getPaidForMonth())
                .build();
    }
    
    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .studentId(payment.getStudent().getId())
                .studentName(payment.getStudent().getFullName())
                .groupId(payment.getGroup().getId())
                .groupName(payment.getGroup().getName())
                .amount(payment.getAmount())
                .paidForMonth(payment.getPaidForMonth())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
