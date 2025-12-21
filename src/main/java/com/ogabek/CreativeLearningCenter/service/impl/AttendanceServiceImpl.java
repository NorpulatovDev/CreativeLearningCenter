package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.AttendanceRequest;
import com.ogabek.CreativeLearningCenter.dto.request.AttendanceUpdateRequest;
import com.ogabek.CreativeLearningCenter.dto.response.AttendanceResponse;
import com.ogabek.CreativeLearningCenter.entity.Attendance;
import com.ogabek.CreativeLearningCenter.entity.AttendanceStatus;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.exception.BadRequestException;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.AttendanceMapper;
import com.ogabek.CreativeLearningCenter.repository.AttendanceRepository;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentGroupRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import com.ogabek.CreativeLearningCenter.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final AttendanceMapper attendanceMapper;
    
    @Override
    public List<AttendanceResponse> createForGroup(AttendanceRequest request) {
        log.info("Creating attendance for group {} on date {}", request.getGroupId(), request.getDate());
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", request.getGroupId()));
        
        if (attendanceRepository.existsByGroupIdAndDate(request.getGroupId(), request.getDate())) {
            throw new BadRequestException("Attendance already exists for this group and date");
        }
        
        // Get students enrolled in this group via StudentGroup junction table
        List<Student> students = studentGroupRepository.findActiveStudentsByGroupId(request.getGroupId());
        if (students.isEmpty()) {
            throw new BadRequestException("No students enrolled in this group");
        }
        
        Set<Long> absentIds = request.getAbsentStudentIds() != null 
                ? new HashSet<>(request.getAbsentStudentIds()) 
                : new HashSet<>();
        
        List<Attendance> attendances = new ArrayList<>();
        
        for (Student student : students) {
            AttendanceStatus status = absentIds.contains(student.getId()) 
                    ? AttendanceStatus.ABSENT 
                    : AttendanceStatus.PRESENT;
            
            Attendance attendance = attendanceMapper.toEntity(student, group, request.getDate(), status);
            attendances.add(attendance);
        }
        
        attendances = attendanceRepository.saveAll(attendances);
        log.info("Created {} attendance records for group {}", attendances.size(), request.getGroupId());
        
        return attendances.stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getById(Long id) {
        Attendance attendance = findAttendanceById(id);
        return attendanceMapper.toResponse(attendance);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByGroupAndDate(Long groupId, LocalDate date) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }
        
        return attendanceRepository.findByGroupIdAndDate(groupId, date).stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByMonth(Integer year, Integer month) {
        return attendanceRepository.findByMonth(year, month).stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByGroupIdAndMonth(Long groupId, Integer year, Integer month) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }
        
        return attendanceRepository.findByGroupIdAndMonth(groupId, year, month).stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByStudentIdAndMonth(Long studentId, Integer year, Integer month) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        
        return attendanceRepository.findByStudentIdAndMonth(studentId, year, month).stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByStudentGroupAndMonth(
            Long studentId, Long groupId, Integer year, Integer month) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        return attendanceRepository
                .findByStudentIdAndGroupIdAndMonth(studentId, groupId, year, month)
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }


    @Override
    public AttendanceResponse update(Long id, AttendanceUpdateRequest request) {
        log.info("Updating attendance {} to status {}", id, request.getStatus());
        
        Attendance attendance = findAttendanceById(id);
        attendance.setStatus(request.getStatus());
        attendance = attendanceRepository.save(attendance);
        
        return attendanceMapper.toResponse(attendance);
    }
    
    private Attendance findAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
    }
}
