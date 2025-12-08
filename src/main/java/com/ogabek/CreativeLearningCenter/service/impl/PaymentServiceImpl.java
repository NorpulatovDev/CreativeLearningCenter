package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.PaymentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.PaymentResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Payment;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.PaymentMapper;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import com.ogabek.CreativeLearningCenter.service.PaymentService;
import com.ogabek.CreativeLearningCenter.service.SmsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final PaymentMapper paymentMapper;
    private final SmsNotificationService smsNotificationService;
    
    @Override
    public PaymentResponse create(PaymentRequest request) {
        log.info("Creating payment for student {} in group {}", request.getStudentId(), request.getGroupId());
        
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", request.getGroupId()));
        
        Payment payment = paymentMapper.toEntity(request, student, group);
        payment = paymentRepository.save(payment);
        
        log.info("Payment created with id: {}", payment.getId());
        
        // Send SMS notification
        smsNotificationService.sendPaymentNotification(student, payment);
        
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        
        return paymentRepository.findByStudentId(studentId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByGroupId(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }
        
        return paymentRepository.findByGroupId(groupId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}