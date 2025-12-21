package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.StudentGroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentGroupResponse;
import com.ogabek.CreativeLearningCenter.service.impl.StudentGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    @PostMapping
    public ResponseEntity<StudentGroupResponse> addStudentToGroup(
            @Valid @RequestBody StudentGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentGroupService.addStudentToGroup(request));
    }

    @DeleteMapping("/student/{studentId}/group/{groupId}")
    public ResponseEntity<Void> removeStudentFromGroup(
            @PathVariable Long studentId,
            @PathVariable Long groupId) {
        studentGroupService.removeStudentFromGroup(studentId, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentGroupResponse>> getStudentGroups(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(studentGroupService.getStudentGroups(studentId));
    }

    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<StudentGroupResponse>> getStudentActiveGroups(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(studentGroupService.getStudentActiveGroups(studentId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StudentGroupResponse>> getGroupStudents(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(studentGroupService.getGroupStudents(groupId));
    }
}
