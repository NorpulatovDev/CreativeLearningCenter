package com.example.CreativeLearningCenter.controller;

import com.example.CreativeLearningCenter.dto.TeacherDto;
import com.example.CreativeLearningCenter.dto.TeacherCreateRequest;
import com.example.CreativeLearningCenter.dto.TeacherUpdateRequest;
import com.example.CreativeLearningCenter.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    
    @GetMapping
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }
    
    @PostMapping
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(teacherService.createTeacher(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDto> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherUpdateRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, request));
    }
}