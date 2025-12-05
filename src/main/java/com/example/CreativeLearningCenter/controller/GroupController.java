package com.example.CreativeLearningCenter.controller;

import com.example.CreativeLearningCenter.dto.GroupDto;
import com.example.CreativeLearningCenter.dto.GroupCreateRequest;
import com.example.CreativeLearningCenter.dto.GroupUpdateRequest;
import com.example.CreativeLearningCenter.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    
    private final GroupService groupService;
    
    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<GroupDto>> getGroupsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(groupService.getGroupsByTeacher(teacherId));
    }
    
    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(groupService.createGroup(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody GroupUpdateRequest request) {
        return ResponseEntity.ok(groupService.updateGroup(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}