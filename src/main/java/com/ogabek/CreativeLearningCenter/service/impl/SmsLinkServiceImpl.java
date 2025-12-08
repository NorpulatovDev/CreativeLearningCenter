package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.SmsLinkByCodeRequest;
import com.ogabek.CreativeLearningCenter.dto.request.SmsLinkByPhoneRequest;
import com.ogabek.CreativeLearningCenter.dto.response.SmsLinkResponse;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.StudentMapper;
import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import com.ogabek.CreativeLearningCenter.service.SmsLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SMS Link Service Implementation
 * 
 * Decision: When /sms/link/by-phone matches multiple students with the same phone number,
 * ALL matching students will be linked and returned in the response list.
 * This allows parents with multiple children to link all at once.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmsLinkServiceImpl implements SmsLinkService {
    
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    
    @Override
    public List<SmsLinkResponse> linkByPhone(SmsLinkByPhoneRequest request) {
        log.info("Linking SMS by phone: {}", request.getPhoneNumber());
        
        List<Student> students = studentRepository.findByParentPhoneNumber(request.getPhoneNumber());
        
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("Student", "parentPhoneNumber", request.getPhoneNumber());
        }
        
        // Link all students with this phone number
        for (Student student : students) {
            student.setSmsLinked(true);
            studentRepository.save(student);
            log.info("SMS linked for student: {} ({})", student.getFullName(), student.getSmsLinkCode());
        }
        
        return students.stream()
                .map(studentMapper::toSmsLinkResponse)
                .toList();
    }
    
    @Override
    public SmsLinkResponse linkByCode(SmsLinkByCodeRequest request) {
        log.info("Linking SMS by code: {}", request.getCode());
        
        Student student = studentRepository.findBySmsLinkCode(request.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "smsLinkCode", request.getCode()));
        
        // Update phone number to the provided one and set smsLinked = true
        // Decision: Update the saved phone to the provided one for flexibility
        student.setParentPhoneNumber(request.getPhoneNumber());
        student.setSmsLinked(true);
        student = studentRepository.save(student);
        
        log.info("SMS linked for student: {} with phone: {}", student.getFullName(), request.getPhoneNumber());
        
        return studentMapper.toSmsLinkResponse(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SmsLinkResponse getLinkStatus(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        
        return studentMapper.toSmsLinkResponse(student);
    }
}