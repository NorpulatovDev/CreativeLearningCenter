package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.TelegramLinkByCodeRequest;
import com.example.CreativeLearningCenter.dto.TelegramLinkResponseDto;
import com.example.CreativeLearningCenter.entity.Student;
import com.example.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.example.CreativeLearningCenter.dto.TelegramLinkByPhoneRequest;
import com.example.CreativeLearningCenter.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramLinkingService {
    
    private final StudentRepository studentRepository;
    
    @Transactional
    public TelegramLinkResponseDto linkByPhone(TelegramLinkByPhoneRequest request) {
        Student student = studentRepository.findByParentPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student not found with parent phone: " + request.getPhoneNumber()));
        
        return linkStudent(student, request.getChatId());
    }
    
    @Transactional
    public TelegramLinkResponseDto linkByCode(TelegramLinkByCodeRequest request) {
        Student student = studentRepository.findByTelegramLinkCode(request.getCode())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student not found with link code: " + request.getCode()));
        
        return linkStudent(student, request.getChatId());
    }
    
    public TelegramLinkResponseDto getLinkInfo(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        return new TelegramLinkResponseDto(
            student.getId(),
            student.getParentPhoneNumber(),
            student.getTelegramLinked(),
            student.getTelegramChatId(),
            student.getTelegramLinkCode(),
            "Link information retrieved successfully"
        );
    }
    
    private TelegramLinkResponseDto linkStudent(Student student, Long chatId) {
        student.setTelegramChatId(chatId);
        student.setTelegramLinked(true);
        
        Student saved = studentRepository.save(student);
        log.info("Telegram linked for student: {} with chat id: {}", saved.getId(), chatId);
        
        return new TelegramLinkResponseDto(
            saved.getId(),
            saved.getParentPhoneNumber(),
            saved.getTelegramLinked(),
            saved.getTelegramChatId(),
            saved.getTelegramLinkCode(),
            "Telegram account linked successfully"
        );
    }
}