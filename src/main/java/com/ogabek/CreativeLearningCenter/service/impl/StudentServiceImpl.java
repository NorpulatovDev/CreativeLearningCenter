package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.StudentMapper;
import com.ogabek.CreativeLearningCenter.repository.*;
import com.ogabek.CreativeLearningCenter.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final GroupRepository groupRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponse create(StudentRequest request) {
        log.info("Creating student: {}", request.getFullName());

        Student student = studentMapper.toEntity(request);
        // Generate a unique code for reference
        student.setSmsLinkCode("STU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        student = studentRepository.save(student);
        log.info("Student created with id: {}", student.getId());

        return studentMapper.toResponse(student, BigDecimal.ZERO, List.of(), null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        Student student = findStudentById(id);
        BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(id);
        List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(id);
        return studentMapper.toResponse(student, totalPaid, activeGroups, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        log.info("Fetching all students with optimized queries");

        // Fetch all students with their groups, teachers in ONE query
        List<Student> students = studentRepository.findAllWithGroups();
        log.info("Fetched {} students", students.size());

        // Fetch all payment totals in ONE query
        Map<Long, BigDecimal> paymentTotals = paymentRepository.getTotalPaidGroupedByStudent()
                .stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (BigDecimal) arr[1]
                ));
        log.info("Fetched payment totals for {} students", paymentTotals.size());

        // Map to responses efficiently
        return students.stream()
                .map(student -> {
                    BigDecimal totalPaid = paymentTotals.getOrDefault(student.getId(), BigDecimal.ZERO);
                    // Get active groups from already-loaded collection (no DB query)
                    List<StudentGroup> activeGroups = student.getStudentGroups().stream()
                            .filter(StudentGroup::getActive)
                            .toList();
                    return studentMapper.toResponse(student, totalPaid, activeGroups, null, null);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getByGroupId(Long groupId, Integer year, Integer month) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", groupId);
        }

        // If year/month not provided, use current date
        LocalDate checkDate = LocalDate.now();
        if (year != null && month != null) {
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("Month must be between 1 and 12");
            }
            checkDate = LocalDate.of(year, month, 1);
        }
        
        final Integer finalYear = checkDate.getYear();
        final Integer finalMonth = checkDate.getMonthValue();
        
        log.info("Fetching students for group {} with payment check for {}-{}", 
                groupId, finalYear, finalMonth);

        // Get students enrolled in this group via StudentGroup junction table
        List<StudentGroup> enrollments = studentGroupRepository.findByGroupIdAndActiveTrue(groupId);

        return enrollments.stream()
                .map(enrollment -> {
                    Student student = enrollment.getStudent();
                    BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(student.getId());
                    List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(student.getId());
                    return studentMapper.toResponse(student, totalPaid, activeGroups, finalYear, finalMonth);
                })
                .toList();
    }

    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        log.info("Updating student: {}", id);

        Student student = findStudentById(id);
        studentMapper.updateEntity(student, request);

        student = studentRepository.save(student);
        BigDecimal totalPaid = paymentRepository.getTotalPaidByStudentId(id);
        List<StudentGroup> activeGroups = studentGroupRepository.findByStudentIdAndActiveTrue(id);

        return studentMapper.toResponse(student, totalPaid, activeGroups, null, null);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting student {} with all related data", id);

        Student student = findStudentById(id);

        // Delete all related data in correct order to avoid foreign key conflicts
        // The Student entity has cascade=CascadeType.ALL and orphanRemoval=true,
        // but we'll manually delete to ensure proper ordering and logging

        // 1. Delete attendance records first (they reference student and group)
        log.info("Deleting attendance records for student {}", id);
        int attendanceCount = attendanceRepository.findByStudentId(id).size();
        attendanceRepository.findByStudentId(id).forEach(attendanceRepository::delete);
        log.info("Deleted {} attendance records for student {}", attendanceCount, id);

        // 2. Delete payments (they reference student and group)
        log.info("Deleting payments for student {}", id);
        List<Long> paymentIds = paymentRepository.findByStudentId(id)
                .stream()
                .map(payment -> payment.getId())
                .toList();

        paymentIds.forEach(paymentRepository::deleteById);
        log.info("Deleted {} payments for student {}", paymentIds.size(), id);

        // 3. Delete student-group enrollments (they reference student and group)
        log.info("Deleting enrollments for student {}", id);
        List<Long> enrollmentIds = studentGroupRepository.findByStudentId(id)
                .stream()
                .map(StudentGroup::getId)
                .toList();

        enrollmentIds.forEach(studentGroupRepository::deleteById);
        log.info("Deleted {} enrollments for student {}", enrollmentIds.size(), id);

        // 4. Finally delete the student
        studentRepository.delete(student);

        log.info("Student {} and all related data deleted successfully", id);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }
}