package com.example.CreativeLearningCenter.controller;

import com.example.CreativeLearningCenter.dto.StudentCreateRequest;
import com.example.CreativeLearningCenter.dto.StudentUpdateRequest;
import com.example.CreativeLearningCenter.dto.StudentDto;
import com.example.CreativeLearningCenter.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StudentDto>> getStudentsByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(studentService.getStudentsByGroup(groupId));
    }
    
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(studentService.createStudent(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }
    
    @DeleteMapping("/{id}/group")
    public ResponseEntity<Void> detachFromGroup(@PathVariable Long id) {
        studentService.detachStudentFromGroup(id);
        return ResponseEntity.noContent().build();
    }
}