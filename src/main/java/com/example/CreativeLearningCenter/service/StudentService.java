package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.StudentDto;
import com.example.CreativeLearningCenter.dto.StudentCreateRequest;
import com.example.CreativeLearningCenter.dto.StudentUpdateRequest;
import com.example.CreativeLearningCenter.entity.Student;
import com.example.CreativeLearningCenter.entity.Group;
import com.example.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.example.CreativeLearningCenter.mapper.EntityMapper;
import com.example.CreativeLearningCenter.repository.GroupRepository;
import com.example.CreativeLearningCenter.repository.StudentRepository;
import com.example.CreativeLearningCenter.repository.PaymentRepository;
import com.example.CreativeLearningCenter.util.TelegramCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final PaymentRepository paymentRepository;
    private final EntityMapper mapper;
    private final TelegramCodeGenerator codeGenerator;
    
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
            .peek(this::calculateTotalPaid)
            .map(mapper::toStudentDto)
            .collect(Collectors.toList());
    }
    
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        calculateTotalPaid(student);
        return mapper.toStudentDto(student);
    }
    
    @Transactional
    public StudentDto createStudent(StudentCreateRequest request) {
        if (studentRepository.existsByParentPhoneNumber(request.getParentPhoneNumber())) {
            throw new IllegalArgumentException("Student with this parent phone already exists");
        }
        
        Student student = mapper.toStudent(request);
        student.setTelegramLinkCode(codeGenerator.generateCode());
        
        Student saved = studentRepository.save(student);
        calculateTotalPaid(saved);
        log.info("Student created with id: {}, code: {}", saved.getId(), saved.getTelegramLinkCode());
        return mapper.toStudentDto(saved);
    }
    
    @Transactional
    public StudentDto updateStudent(Long id, StudentUpdateRequest request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        mapper.updateStudentFromRequest(student, request);
        
        if (request.getActiveGroupId() != null) {
            Group group = groupRepository.findById(request.getActiveGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            student.setActiveGroup(group);
        }
        
        Student updated = studentRepository.save(student);
        calculateTotalPaid(updated);
        return mapper.toStudentDto(updated);
    }
    
    @Transactional
    public void detachStudentFromGroup(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        student.setActiveGroup(null);
        studentRepository.save(student);
        log.info("Student {} detached from group", studentId);
    }
    
    public List<StudentDto> getStudentsByGroup(Long groupId) {
        return studentRepository.findByActiveGroupId(groupId).stream()
            .peek(this::calculateTotalPaid)
            .map(mapper::toStudentDto)
            .collect(Collectors.toList());
    }
    
    private void calculateTotalPaid(Student student) {
        BigDecimal totalPaid = paymentRepository.getTotalPaidByStudent(student.getId());
        student.setTotalPaid(totalPaid != null ? totalPaid : BigDecimal.ZERO);
    }
}