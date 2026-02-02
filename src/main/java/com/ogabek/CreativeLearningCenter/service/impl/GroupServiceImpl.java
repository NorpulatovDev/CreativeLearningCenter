package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.GroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.GroupResponse;
import com.ogabek.CreativeLearningCenter.entity.Group;
import com.ogabek.CreativeLearningCenter.entity.Teacher;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.GroupMapper;
import com.ogabek.CreativeLearningCenter.repository.AttendanceRepository;
import com.ogabek.CreativeLearningCenter.repository.GroupRepository;
import com.ogabek.CreativeLearningCenter.repository.PaymentRepository;
import com.ogabek.CreativeLearningCenter.repository.StudentGroupRepository;
import com.ogabek.CreativeLearningCenter.repository.TeacherRepository;
import com.ogabek.CreativeLearningCenter.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupServiceImpl implements GroupService {
    
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final GroupMapper groupMapper;
    
    @Override
    public GroupResponse create(GroupRequest request) {
        log.info("Creating group: {}", request.getName());
        
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.getTeacherId()));
        
        Group group = groupMapper.toEntity(request, teacher);
        group = groupRepository.save(group);
        
        log.info("Group created with id: {}", group.getId());
        return groupMapper.toResponse(group, 0, BigDecimal.ZERO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GroupResponse getById(Long id) {
        Group group = findGroupById(id);
        int activeStudentsCount = studentGroupRepository.countActiveByGroupId(id);
        BigDecimal totalPaid = paymentRepository.getTotalPaidByGroupId(id);
        return groupMapper.toResponse(group, activeStudentsCount, totalPaid);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getAll() {
        return groupRepository.findAll().stream()
                .map(group -> {
                    int activeStudentsCount = studentGroupRepository.countActiveByGroupId(group.getId());
                    BigDecimal totalPaid = paymentRepository.getTotalPaidByGroupId(group.getId());
                    return groupMapper.toResponse(group, activeStudentsCount, totalPaid);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getAllSortedByTeacher() {
        // Get current month in YYYY-MM format
        LocalDate now = LocalDate.now();
        String currentMonth = now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        return groupRepository.findAllByOrderByTeacherIdAscNameAsc().stream()
                .map(group -> {
                    int activeStudentsCount = studentGroupRepository.countActiveByGroupId(group.getId());
                    // Use current month instead of total
                    BigDecimal totalPaid = paymentRepository.getTotalPaidByGroupIdAndMonth(
                            group.getId(), currentMonth);
                    return groupMapper.toResponse(group, activeStudentsCount, totalPaid);
                })
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getByTeacherId(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher", teacherId);
        }
        
        return groupRepository.findByTeacherIdOrderByNameAsc(teacherId).stream()
                .map(group -> {
                    int activeStudentsCount = studentGroupRepository.countActiveByGroupId(group.getId());
                    BigDecimal totalPaid = paymentRepository.getTotalPaidByGroupId(group.getId());
                    return groupMapper.toResponse(group, activeStudentsCount, totalPaid);
                })
                .toList();
    }
    
    @Override
    public GroupResponse update(Long id, GroupRequest request) {
        log.info("Updating group: {}", id);
        
        Group group = findGroupById(id);
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", request.getTeacherId()));
        
        groupMapper.updateEntity(group, request, teacher);
        group = groupRepository.save(group);
        
        int activeStudentsCount = studentGroupRepository.countActiveByGroupId(id);
        BigDecimal totalPaid = paymentRepository.getTotalPaidByGroupId(id);
        return groupMapper.toResponse(group, activeStudentsCount, totalPaid);
    }
    
    @Override
    public void delete(Long id) {
        log.info("Deleting group: {}", id);
        
        Group group = findGroupById(id);
        
        studentGroupRepository.deleteByGroupId(id);
        attendanceRepository.deleteByGroupId(id);
        paymentRepository.deleteByGroupId(id);
        groupRepository.delete(group);
        
        log.info("Group deleted: {}", id);
    }
    
    private Group findGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));
    }
}
