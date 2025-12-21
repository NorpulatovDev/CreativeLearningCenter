package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.StudentGroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentGroupResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import com.ogabek.CreativeLearningCenter.exception.BadRequestException;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentGroupRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentGroupService {
    
    private final StudentGroupRepository studentGroupRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    
    public StudentGroupResponse addStudentToGroup(StudentGroupRequest request) {
        log.info("Adding student {} to group {}", request.getStudentId(), request.getGroupId());
        
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", request.getGroupId()));
        
        if (studentGroupRepository.existsByStudentIdAndGroupIdAndActiveTrue(
                request.getStudentId(), request.getGroupId())) {
            throw new BadRequestException("Student is already enrolled in this group");
        }
        
        var existingEnrollment = studentGroupRepository
                .findByStudentIdAndGroupId(request.getStudentId(), request.getGroupId());
        
        StudentGroup studentGroup;
        if (existingEnrollment.isPresent()) {
            studentGroup = existingEnrollment.get();
            studentGroup.setActive(true);
            studentGroup.setLeftAt(null);
            log.info("Reactivated enrollment for student {} in group {}", 
                    request.getStudentId(), request.getGroupId());
        } else {
            studentGroup = StudentGroup.builder()
                    .student(student)
                    .group(group)
                    .active(true)
                    .enrolledAt(LocalDate.now())
                    .build();
        }
        
        studentGroup = studentGroupRepository.save(studentGroup);
        log.info("Student {} enrolled to group {}", request.getStudentId(), request.getGroupId());
        
        return toResponse(studentGroup);
    }
    
    public void removeStudentFromGroup(Long studentId, Long groupId) {
        log.info("Removing student {} from group {}", studentId, groupId);
        
        StudentGroup studentGroup = studentGroupRepository.findByStudentIdAndGroupId(studentId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        
        studentGroup.setActive(false);
        studentGroup.setLeftAt(LocalDate.now());
        studentGroupRepository.save(studentGroup);
        
        log.info("Student {} removed from group {}", studentId, groupId);
    }
    
    @Transactional(readOnly = true)
    public List<StudentGroupResponse> getStudentGroups(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        return studentGroupRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<StudentGroupResponse> getStudentActiveGroups(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        return studentGroupRepository.findByStudentIdAndActiveTrue(studentId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<StudentGroupResponse> getGroupStudents(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }
        return studentGroupRepository.findByGroupIdAndActiveTrue(groupId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public int countActiveStudentsInGroup(Long groupId) {
        return studentGroupRepository.countActiveByGroupId(groupId);
    }
    
    @Transactional(readOnly = true)
    public boolean isStudentEnrolledInGroup(Long studentId, Long groupId) {
        return studentGroupRepository.existsByStudentIdAndGroupIdAndActiveTrue(studentId, groupId);
    }
    
    private StudentGroupResponse toResponse(StudentGroup sg) {
        return StudentGroupResponse.builder()
                .id(sg.getId())
                .studentId(sg.getStudent().getId())
                .studentName(sg.getStudent().getFullName())
                .groupId(sg.getGroup().getId())
                .groupName(sg.getGroup().getName())
                .teacherName(sg.getGroup().getTeacher().getFullName())
                .monthlyFee(sg.getGroup().getMonthlyFee())
                .active(sg.getActive())
                .enrolledAt(sg.getEnrolledAt())
                .leftAt(sg.getLeftAt())
                .build();
    }
}
