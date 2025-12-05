package com.example.CreativeLearningCenter.mapper;

import com.example.CreativeLearningCenter.dto.*;
import com.example.CreativeLearningCenter.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class EntityMapper {
    
    // Student Mappers
    public StudentDto toStudentDto(Student student) {
        return new StudentDto(
            student.getId(),
            student.getFullName(),
            student.getParentName(),
            student.getParentPhoneNumber(),
            student.getTelegramChatId(),
            student.getTelegramLinked(),
            student.getTelegramLinkCode(),
            student.getActiveGroup() != null ? student.getActiveGroup().getId() : null,
            student.getTotalPaid()
        );
    }
    
    public Student toStudent(StudentCreateRequest request) {
        Student student = new Student();
        student.setFullName(request.getFullName());
        student.setParentName(request.getParentName());
        student.setParentPhoneNumber(request.getParentPhoneNumber());
        student.setTelegramLinked(false);
        return student;
    }
    
    public void updateStudentFromRequest(Student student, StudentUpdateRequest request) {
        if (request.getFullName() != null) student.setFullName(request.getFullName());
        if (request.getParentName() != null) student.setParentName(request.getParentName());
        if (request.getParentPhoneNumber() != null) student.setParentPhoneNumber(request.getParentPhoneNumber());
    }
    
    // Teacher Mappers
    public TeacherDto toTeacherDto(Teacher teacher) {
        return new TeacherDto(
            teacher.getId(),
            teacher.getFullName(),
            teacher.getPhoneNumber(),
            teacher.getTotalIncome()
        );
    }
    
    public Teacher toTeacher(TeacherCreateRequest request) {
        Teacher teacher = new Teacher();
        teacher.setFullName(request.getFullName());
        teacher.setPhoneNumber(request.getPhoneNumber());
        return teacher;
    }
    
    // Group Mappers
    public GroupDto toGroupDto(Group group) {
        return new GroupDto(
            group.getId(),
            group.getName(),
            group.getTeacher().getId(),
            group.getTeacher().getFullName(),
            group.getMonthlyFee(),
            group.getCreatedAt(),
            (long) (group.getStudents() != null ? group.getStudents().size() : 0),
            calculateTotalAmountToPay(group),
            group.getTotalPaid()
        );
    }
    
    public Group toGroup(GroupCreateRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setMonthlyFee(request.getMonthlyFee());
        return group;
    }
    
    // Attendance Mappers
    public AttendanceDto toAttendanceDto(Attendance attendance) {
        return new AttendanceDto(
            attendance.getId(),
            attendance.getDate(),
            attendance.getStudent().getId(),
            attendance.getStudent().getFullName(),
            attendance.getGroup().getId(),
            attendance.getStatus()
        );
    }
    
    public Attendance toAttendance(AttendanceCreateRequest request, Student student, Group group) {
        Attendance attendance = new Attendance();
        attendance.setDate(request.getDate());
        attendance.setStudent(student);
        attendance.setGroup(group);
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        return attendance;
    }
    
    // Payment Mappers
    public PaymentDto toPaymentDto(Payment payment) {
        return new PaymentDto(
            payment.getId(),
            payment.getStudent().getId(),
            payment.getStudent().getFullName(),
            payment.getGroup().getId(),
            payment.getGroup().getName(),
            payment.getAmount(),
            payment.getPaidForMonth(),
            payment.getPaidAt()
        );
    }
    
    public Payment toPayment(PaymentCreateRequest request, Student student, Group group) {
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setGroup(group);
        payment.setAmount(request.getAmount());
        payment.setPaidForMonth(request.getPaidForMonth());
        payment.setPaidAt(java.time.LocalDate.now());
        return payment;
    }
    
    // Helper Methods
    private BigDecimal calculateTotalAmountToPay(Group group) {
        if (group.getStudents() == null || group.getStudents().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return group.getMonthlyFee().multiply(BigDecimal.valueOf(group.getStudents().size()));
    }
}