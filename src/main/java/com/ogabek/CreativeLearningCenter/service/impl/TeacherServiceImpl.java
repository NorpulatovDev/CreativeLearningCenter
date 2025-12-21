package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.TeacherRequest;
import com.ogabek.CreativeLearningCenter.dto.response.TeacherResponse;
import com.ogabek.CreativeLearningCenter.entity.Teacher;
import com.ogabek.CreativeLearningCenter.exception.BadRequestException;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.TeacherMapper;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
import com.ogabek.CreativeLearningCenter.repository.TeacherRepository;
import com.ogabek.CreativeLearningCenter.service.TeacherService;
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
public class TeacherServiceImpl implements TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final PaymentRepository paymentRepository;
    private final TeacherMapper teacherMapper;
    
    @Override
    public TeacherResponse create(TeacherRequest request) {
        log.info("Creating teacher: {}", request.getFullName());
        
        Teacher teacher = teacherMapper.toEntity(request);
        teacher = teacherRepository.save(teacher);
        
        log.info("Teacher created with id: {}", teacher.getId());
        return teacherMapper.toResponse(teacher, BigDecimal.ZERO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getById(Long id) {
        Teacher teacher = findTeacherById(id);
        BigDecimal totalIncome = calculateTotalIncome(id);
        return teacherMapper.toResponse(teacher, totalIncome);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAll() {
        return teacherRepository.findAll().stream()
                .map(teacher -> {
                    BigDecimal totalIncome = calculateTotalIncome(teacher.getId());
                    return teacherMapper.toResponse(teacher, totalIncome);
                })
                .toList();
    }
    
    @Override
    public TeacherResponse update(Long id, TeacherRequest request) {
        log.info("Updating teacher: {}", id);
        
        Teacher teacher = findTeacherById(id);
        teacherMapper.updateEntity(teacher, request);
        teacher = teacherRepository.save(teacher);
        
        BigDecimal totalIncome = calculateTotalIncome(id);
        return teacherMapper.toResponse(teacher, totalIncome);
    }
    
    @Override
    public void delete(Long id) {
        log.info("Deleting teacher: {}", id);
        
        Teacher teacher = findTeacherById(id);
        
        if (!groupRepository.findByTeacherId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete teacher with assigned groups");
        }
        
        teacherRepository.delete(teacher);
        log.info("Teacher deleted: {}", id);
    }
    
    private Teacher findTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", id));
    }
    
    private BigDecimal calculateTotalIncome(Long teacherId) {
        return groupRepository.findByTeacherId(teacherId).stream()
                .map(group -> paymentRepository.getTotalPaidByGroupId(group.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
