package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;
import com.ogabek.CreativeLearningCenter.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        return ResponseEntity.ok(studentService.getAll());
    }
    
    @GetMapping("/group/{groupId}")
    @Operation(summary = "Guruh bo'yicha o'quvchilarni olish", 
               description = "Ma'lum bir guruhdagi o'quvchilarni olish. Year va month parametrlari optional - agar berilmasa, joriy oy ishlatiladi.")
    public ResponseEntity<List<StudentResponse>> getByGroupId(
            @PathVariable Long groupId,
            @Parameter(description = "Yil (optional, default: joriy yil)")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Oy (1-12, optional, default: joriy oy)")
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(studentService.getByGroupId(groupId, year, month));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(@PathVariable Long id, 
                                                   @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}