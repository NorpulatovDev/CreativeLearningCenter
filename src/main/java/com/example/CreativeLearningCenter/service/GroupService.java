package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.dto.GroupDto;
import com.example.CreativeLearningCenter.dto.GroupCreateRequest;
import com.example.CreativeLearningCenter.dto.GroupUpdateRequest;
import com.example.CreativeLearningCenter.entity.*;
import com.example.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.example.CreativeLearningCenter.mapper.EntityMapper;
import com.example.CreativeLearningCenter.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final EntityMapper mapper;
    
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
            .peek(this::enrichGroupData)
            .map(mapper::toGroupDto)
            .collect(Collectors.toList());
    }
    
    public List<GroupDto> getGroupsByTeacher(Long teacherId) {
        return groupRepository.findByTeacherId(teacherId).stream()
            .peek(this::enrichGroupData)
            .map(mapper::toGroupDto)
            .collect(Collectors.toList());
    }
    
    public GroupDto getGroupById(Long id) {
        Group group = groupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        enrichGroupData(group);
        return mapper.toGroupDto(group);
    }
    
    @Transactional
    public GroupDto createGroup(GroupCreateRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        
        Group group = mapper.toGroup(request);
        group.setTeacher(teacher);
        
        Group saved = groupRepository.save(group);
        enrichGroupData(saved);
        log.info("Group created with id: {}", saved.getId());
        return mapper.toGroupDto(saved);
    }
    
    @Transactional
    public GroupDto updateGroup(Long id, GroupUpdateRequest request) {
        Group group = groupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        
        if (request.getName() != null) group.setName(request.getName());
        if (request.getMonthlyFee() != null) group.setMonthlyFee(request.getMonthlyFee());
        
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            group.setTeacher(teacher);
        }
        
        Group updated = groupRepository.save(group);
        enrichGroupData(updated);
        return mapper.toGroupDto(updated);
    }
    
    @Transactional
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        
        // Detach all students
        List<Student> students = studentRepository.findByActiveGroupId(id);
        for (Student student : students) {
            student.setActiveGroup(null);
            studentRepository.save(student);
        }
        
        // Delete all attendances for this group
        List<Attendance> attendances =
            attendanceRepository.findByGroupId(id);
        attendanceRepository.deleteAll(attendances);
        
        // Delete all payments for this group
        List<Payment> payments =
            paymentRepository.findByGroupId(id);
        paymentRepository.deleteAll(payments);
        
        // Delete group
        groupRepository.delete(group);
        log.info("Group deleted with id: {}", id);
    }
    
    private void enrichGroupData(Group group) {
        // Set students count
        long count = (group.getStudents() != null) ? group.getStudents().size() : 0;
        group.setStudentsCount(count);
        
        // Calculate totalAmountToPay
        BigDecimal totalAmountToPay = group.getMonthlyFee().multiply(BigDecimal.valueOf(count));
        group.setTotalAmountToPay(totalAmountToPay);
        
        // Calculate totalPaid
        BigDecimal totalPaid = paymentRepository.getTotalPaidByGroup(group.getId());
        group.setTotalPaid(totalPaid != null ? totalPaid : BigDecimal.ZERO);
    }
}