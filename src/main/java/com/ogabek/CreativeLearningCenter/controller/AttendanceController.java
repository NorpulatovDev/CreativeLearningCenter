package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.AttendanceRequest;
import com.ogabek.CreativeLearningCenter.dto.request.AttendanceUpdateRequest;
import com.ogabek.CreativeLearningCenter.dto.response.AttendanceResponse;
import com.ogabek.CreativeLearningCenter.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @PostMapping
    public ResponseEntity<List<AttendanceResponse>> create(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createForGroup(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getById(id));
    }
    
    @GetMapping("/group/{groupId}/date/{date}")
    public ResponseEntity<List<AttendanceResponse>> getByGroupAndDate(
            @PathVariable Long groupId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getByGroupAndDate(groupId, date));
    }
    
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<AttendanceResponse>> getByMonth(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(attendanceService.getByMonth(year, month));
    }
    
    @GetMapping("/group/{groupId}/month/{year}/{month}")
    public ResponseEntity<List<AttendanceResponse>> getByGroupIdAndMonth(
            @PathVariable Long groupId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(attendanceService.getByGroupIdAndMonth(groupId, year, month));
    }
    
    @GetMapping("/student/{studentId}/month/{year}/{month}")
    public ResponseEntity<List<AttendanceResponse>> getByStudentIdAndMonth(
            @PathVariable Long studentId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(attendanceService.getByStudentIdAndMonth(studentId, year, month));
    }

    @GetMapping("/student/{studentId}/group/{groupId}/month/{year}/{month}")
    public ResponseEntity<List<AttendanceResponse>> getByStudentGroupAndMonth(
            @PathVariable Long studentId,
            @PathVariable Long groupId,
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        List<AttendanceResponse> attendances = attendanceService
                .getByStudentGroupAndMonth(studentId, groupId, year, month);
        return ResponseEntity.ok(attendances);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AttendanceResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody AttendanceUpdateRequest request) {
        return ResponseEntity.ok(attendanceService.update(id, request));
    }
}
