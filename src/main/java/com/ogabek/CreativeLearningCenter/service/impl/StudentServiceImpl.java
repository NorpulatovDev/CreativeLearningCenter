package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.StudentMapper;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentGroupRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import com.ogabek.CreativeLearningCenter.service.StudentService;
import com.ogabek.CreativeLearningCenter.util.SmsCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final GroupRepository groupRepository;
    private final PaymentRepository paymentRepository;
    private final StudentMapper studentMapper;
    private final SmsCodeGenerator smsCodeGenerator;
    
    @Override
    public StudentResponse create(StudentRequest request) {
        log.info("Creating student: {}", request.getFullName());
        
        Student student = studentMapper.toEntity(request);
        student.setSmsLinkCode(smsCodeGenerator.generateUniqueSmsLinkCode());
        student.setSmsLinked(false);
        
        student = studentRepository.save(student);
        log.info("Student created with id: {} and smsLinkCode: {}", student.getId(), student.getSmsLinkCode());
        
        return studentMapper.toResponse(student, BigDecimal.ZERO, List.of());
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        Student student = findStudentById(id);
        BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(id);
        List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(id);
        return studentMapper.toResponse(student, totalPaid, activeGroups);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(student.getId());
                    List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(student.getId());
                    return studentMapper.toResponse(student, totalPaid, activeGroups);
                })
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getByGroupId(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }
        
        // Get students enrolled in this group via StudentGroup junction table
        List<StudentGroup> enrollments = studentGroupRepository.findByGroupIdAndActiveTrue(groupId);
        
        return enrollments.stream()
                .map(enrollment -> {
                    Student student = enrollment.getStudent();
                    BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(student.getId());
                    List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(student.getId());
                    return studentMapper.toResponse(student, totalPaid, activeGroups);
                })
                .toList();
    }
    
    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        log.info("Updating student: {}", id);
        
        Student student = findStudentById(id);
        studentMapper.updateEntity(student, request);
        
        student = studentRepository.save(student);
        BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(id);
        List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(id);
        
        return studentMapper.toResponse(student, totalPaid, activeGroups);
    }
    
    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }
}