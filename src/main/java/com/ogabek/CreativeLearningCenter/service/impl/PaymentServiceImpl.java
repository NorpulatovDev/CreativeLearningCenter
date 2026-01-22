package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.PaymentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.PaymentResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Payment;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.exception.BadRequestException;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.PaymentMapper;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentGroupRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import com.ogabek.CreativeLearningCenter.service.PaymentService;
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
    private final StudentGroupRepository studentGroupRepository;
    private final PaymentMapper paymentMapper;
    
    @Override
    public PaymentResponse create(PaymentRequest request) {
        log.info("Creating payment for student {} in group {}", request.getStudentId(), request.getGroupId());
        
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", request.getGroupId()));
        
        // Validate student is enrolled in this group
        boolean isEnrolled = studentGroupRepository.existsByStudentIdAndGroupIdAndActiveTrue(
                request.getStudentId(), request.getGroupId());
        
        if (!isEnrolled) {
            // Check if student was ever enrolled (inactive enrollment)
            var enrollment = studentGroupRepository.findByStudentIdAndGroupId(
                    request.getStudentId(), request.getGroupId());
            
            if (enrollment.isEmpty()) {
                throw new BadRequestException(
                        "Student is not enrolled in this group. Please enroll the student first.");
            }
            // Allow payment for inactive enrollment (historical payments)
            log.warn("Accepting payment for inactive enrollment: student {} in group {}", 
                    request.getStudentId(), request.getGroupId());
        }
        
        Payment payment = paymentMapper.toEntity(request, student, group);
        payment = paymentRepository.save(payment);
        
        log.info("Payment created with id: {}", payment.getId());
        
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Payment payment = findPaymentById(id);
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
    
    @Override
    public PaymentResponse update(Long id, PaymentRequest request) {
        log.info("Updating payment {}", id);
        
        Payment payment = findPaymentById(id);
        
        // Check if updating to different student/group
        boolean studentChanged = !payment.getStudent().getId().equals(request.getStudentId());
        boolean groupChanged = !payment.getGroup().getId().equals(request.getGroupId());
        
        if (studentChanged || groupChanged) {
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));
            
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group", request.getGroupId()));
            
            // Validate new student is enrolled in new group
            var enrollment = studentGroupRepository.findByStudentIdAndGroupId(
                    request.getStudentId(), request.getGroupId());
            
            if (enrollment.isEmpty()) {
                throw new BadRequestException(
                        "Student is not enrolled in this group. Cannot update payment.");
            }
            
            payment.setStudent(student);
            payment.setGroup(group);
        }
        
        payment.setAmount(request.getAmount());
        payment.setPaidForMonth(request.getPaidForMonth());
        
        payment = paymentRepository.save(payment);
        log.info("Payment {} updated successfully", id);
        
        return paymentMapper.toResponse(payment);
    }
    
    @Override
    public void delete(Long id) {
        log.info("Deleting payment {}", id);
        
        Payment payment = findPaymentById(id);
        paymentRepository.delete(payment);
        
        log.info("Payment {} deleted successfully", id);
    }
    
    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }
}