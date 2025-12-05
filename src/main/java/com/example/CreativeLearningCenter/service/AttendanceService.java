package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.AttendanceDto;
import com.example.CreativeLearningCenter.dto.AttendanceCreateRequest;
import com.example.CreativeLearningCenter.dto.AttendanceMarkRequest;
import com.example.CreativeLearningCenter.entity.Attendance;
import com.example.CreativeLearningCenter.entity.Student;
import com.example.CreativeLearningCenter.entity.Group;
import com.example.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.example.CreativeLearningCenter.mapper.EntityMapper;
import com.example.CreativeLearningCenter.repository.AttendanceRepository;
import com.example.CreativeLearningCenter.repository.StudentRepository;
import com.example.CreativeLearningCenter.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final EntityMapper mapper;
    private final TelegramNotificationService telegramService;
    
    public List<AttendanceDto> getAttendanceByGroupAndDate(Long groupId, LocalDate date) {
        return attendanceRepository.findByGroupIdAndDate(groupId, date).stream()
            .map(mapper::toAttendanceDto)
            .collect(Collectors.toList());
    }
    
    public List<AttendanceDto> getAttendanceByStudentAndDate(Long studentId, LocalDate date) {
        return attendanceRepository.findByStudentIdAndDate(studentId, date).stream()
            .map(mapper::toAttendanceDto)
            .collect(Collectors.toList());
    }
    
    public List<AttendanceDto> getAttendanceHistoryByGroup(Long groupId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        return attendanceRepository.findByGroupAndMonth(
            groupId, 
            yearMonth.getYear(), 
            yearMonth.getMonthValue()
        ).stream()
            .map(mapper::toAttendanceDto)
            .collect(Collectors.toList());
    }
    
    public List<AttendanceDto> getAttendanceHistoryByStudent(Long studentId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        return attendanceRepository.findByStudentAndMonth(
            studentId, 
            yearMonth.getYear(), 
            yearMonth.getMonthValue()
        ).stream()
            .map(mapper::toAttendanceDto)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public List<AttendanceDto> createAttendanceList(AttendanceCreateRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
            .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        
        // Get all students in group
        List<Student> students = studentRepository.findByActiveGroupId(group.getId());
        
        // Create attendance records - all PRESENT by default
        List<Attendance> attendances = students.stream()
            .map(student -> {
                Attendance attendance = mapper.toAttendance(request, student, group);
                return attendanceRepository.save(attendance);
            })
            .collect(Collectors.toList());
        
        // Mark absents if provided
        if (request.getAbsents() != null && !request.getAbsents().isEmpty()) {
            for (AttendanceMarkRequest absentMark : request.getAbsents()) {
                Attendance attendance = attendanceRepository.findByStudentIdAndGroupIdAndDate(
                    absentMark.getStudentId(),
                    group.getId(),
                    request.getDate()
                ).orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
                
                attendance.setStatus(Attendance.AttendanceStatus.ABSENT);
                attendanceRepository.save(attendance);
                
                // Send notification
                Student student = studentRepository.findById(absentMark.getStudentId()).orElse(null);
                if (student != null) {
                    telegramService.sendAbsenceNotification(student, request.getDate());
                }
            }
        }
        
        return attendances.stream()
            .map(mapper::toAttendanceDto)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public AttendanceDto updateAttendanceStatus(Long attendanceId, Attendance.AttendanceStatus status) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        
        attendance.setStatus(status);
        Attendance updated = attendanceRepository.save(attendance);
        
        // Send notification if marked as ABSENT
        if (status == Attendance.AttendanceStatus.ABSENT) {
            telegramService.sendAbsenceNotification(attendance.getStudent(), attendance.getDate());
        }
        
        return mapper.toAttendanceDto(updated);
    }
    
    public AttendanceDto getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        return mapper.toAttendanceDto(attendance);
    }
}