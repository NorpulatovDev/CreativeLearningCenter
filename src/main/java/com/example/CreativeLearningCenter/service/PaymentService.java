package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.PaymentDto;
import com.example.CreativeLearningCenter.dto.PaymentCreateRequest;
import com.example.CreativeLearningCenter.entity.Payment;
import com.example.CreativeLearningCenter.entity.Student;
import com.example.CreativeLearningCenter.entity.Group;
import com.example.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.example.CreativeLearningCenter.mapper.EntityMapper;
import com.example.CreativeLearningCenter.repository.PaymentRepository;
import com.example.CreativeLearningCenter.repository.StudentRepository;
import com.example.CreativeLearningCenter.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final EntityMapper mapper;
    private final TelegramNotificationService telegramService;
    
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(mapper::toPaymentDto)
            .collect(Collectors.toList());
    }
    
    public List<PaymentDto> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId).stream()
            .map(mapper::toPaymentDto)
            .collect(Collectors.toList());
    }
    
    public List<PaymentDto> getPaymentsByGroup(Long groupId) {
        return paymentRepository.findByGroupId(groupId).stream()
            .map(mapper::toPaymentDto)
            .collect(Collectors.toList());
    }
    
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return mapper.toPaymentDto(payment);
    }
    
    @Transactional
    public PaymentDto createPayment(PaymentCreateRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Group group = groupRepository.findById(request.getGroupId())
            .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        Payment payment = mapper.toPayment(request, student, group);
        Payment saved = paymentRepository.save(payment);
        
        // Send notification to parent
        telegramService.sendPaymentNotification(student, saved);
        
        log.info("Payment created with id: {}, amount: {}", saved.getId(), saved.getAmount());
        return mapper.toPaymentDto(saved);
    }
}