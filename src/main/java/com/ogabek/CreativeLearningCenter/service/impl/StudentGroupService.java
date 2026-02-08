package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.StudentGroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentGroupResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import com.ogabek.CreativeLearningCenter.exception.BadRequestException;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.repository.AttendanceRepository;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
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
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

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

        // Check if student has any other active groups
        long activeGroupsCount = studentGroupRepository.findByStudentIdAndActiveTrue(studentId)
                .stream()
                .filter(StudentGroup::getActive)
                .count();

        if (activeGroupsCount == 0) {
            log.warn("Student {} has no active groups. Deleting student and all related data.", studentId);
            deleteStudentCompletely(studentId);
        }
    }

    private void deleteStudentCompletely(Long studentId) {
        log.info("Deleting student {} with all related data (no active groups)", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        // Delete in correct order to avoid foreign key conflicts

        // 1. Delete attendance records
        log.info("Deleting attendance records for student {}", studentId);
        int attendanceCount = attendanceRepository.findByStudentId(studentId).size();
        attendanceRepository.findByStudentId(studentId).forEach(attendanceRepository::delete);
        log.info("Deleted {} attendance records", attendanceCount);

        // 2. Delete payments
        log.info("Deleting payments for student {}", studentId);
        List<Long> paymentIds = paymentRepository.findByStudentId(studentId)
                .stream()
                .map(payment -> payment.getId())
                .toList();
        paymentIds.forEach(paymentRepository::deleteById);
        log.info("Deleted {} payments", paymentIds.size());

        // 3. Delete student-group enrollments
        log.info("Deleting enrollments for student {}", studentId);
        List<Long> enrollmentIds = studentGroupRepository.findByStudentId(studentId)
                .stream()
                .map(StudentGroup::getId)
                .toList();
        enrollmentIds.forEach(studentGroupRepository::deleteById);
        log.info("Deleted {} enrollments", enrollmentIds.size());

        // 4. Delete the student
        studentRepository.delete(student);

        log.info("Student {} and all related data deleted successfully (no active groups)", studentId);
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