package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.TeacherDto;
import com.example.CreativeLearningCenter.dto.TeacherCreateRequest;
import com.example.CreativeLearningCenter.dto.TeacherUpdateRequest;
import com.example.CreativeLearningCenter.entity.Teacher;
import com.example.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.example.CreativeLearningCenter.mapper.EntityMapper;
import com.example.CreativeLearningCenter.repository.TeacherRepository;
import com.example.CreativeLearningCenter.repository.PaymentRepository;
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
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final PaymentRepository paymentRepository;
    private final EntityMapper mapper;
    
    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAll().stream()
            .peek(this::calculateTotalIncome)
            .map(mapper::toTeacherDto)
            .collect(Collectors.toList());
    }
    
    public TeacherDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        calculateTotalIncome(teacher);
        return mapper.toTeacherDto(teacher);
    }
    
    @Transactional
    public TeacherDto createTeacher(TeacherCreateRequest request) {
        Teacher teacher = mapper.toTeacher(request);
        Teacher saved = teacherRepository.save(teacher);
        calculateTotalIncome(saved);
        log.info("Teacher created with id: {}", saved.getId());
        return mapper.toTeacherDto(saved);
    }
    
    @Transactional
    public TeacherDto updateTeacher(Long id, TeacherUpdateRequest request) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        
        if (request.getFullName() != null) teacher.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) teacher.setPhoneNumber(request.getPhoneNumber());
        
        Teacher updated = teacherRepository.save(teacher);
        calculateTotalIncome(updated);
        return mapper.toTeacherDto(updated);
    }
    
    private void calculateTotalIncome(Teacher teacher) {
        BigDecimal totalIncome = paymentRepository.getTotalIncomeByTeacher(teacher.getId());
        teacher.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
    }
}