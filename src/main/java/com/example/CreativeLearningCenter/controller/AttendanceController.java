package com.example.CreativeLearningCenter.controller;

import com.example.CreativeLearningCenter.dto.AttendanceDto;
import com.example.CreativeLearningCenter.dto.AttendanceCreateRequest;
import com.example.CreativeLearningCenter.entity.Attendance;
import com.example.CreativeLearningCenter.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @GetMapping("/group/{groupId}/date/{date}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByGroupAndDate(
            @PathVariable Long groupId,
            @PathVariable String date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByGroupAndDate(
            groupId, LocalDate.parse(date)));
    }
    
    @GetMapping("/student/{studentId}/date/{date}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByStudentAndDate(
            @PathVariable Long studentId,
            @PathVariable String date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentAndDate(
            studentId, LocalDate.parse(date)));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDto> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }
    
    @GetMapping("/group/{groupId}/month/{month}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceHistoryByGroup(
            @PathVariable Long groupId,
            @PathVariable String month) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistoryByGroup(groupId, month));
    }
    
    @GetMapping("/student/{studentId}/month/{month}")
    public ResponseEntity<List<AttendanceDto>> getAttendanceHistoryByStudent(
            @PathVariable Long studentId,
            @PathVariable String month) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistoryByStudent(studentId, month));
    }
    
    @PostMapping
    public ResponseEntity<List<AttendanceDto>> createAttendanceList(
            @Valid @RequestBody AttendanceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(attendanceService.createAttendanceList(request));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<AttendanceDto> updateAttendanceStatus(
            @PathVariable Long id,
            @RequestParam Attendance.AttendanceStatus status) {
        return ResponseEntity.ok(attendanceService.updateAttendanceStatus(id, status));
    }
}