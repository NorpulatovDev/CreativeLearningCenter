package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.GroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.GroupResponse;
import com.ogabek.CreativeLearningCenter.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    
    private final GroupService groupService;
    
    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAll() {
        return ResponseEntity.ok(groupService.getAll());
    }
    
    @GetMapping("/sorted-by-teacher")
    public ResponseEntity<List<GroupResponse>> getAllSortedByTeacher() {
        return ResponseEntity.ok(groupService.getAllSortedByTeacher());
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<GroupResponse>> getByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(groupService.getByTeacherId(teacherId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> update(@PathVariable Long id, 
                                                 @Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(groupService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
