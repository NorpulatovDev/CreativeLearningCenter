package com.example.CreativeLearningCenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "attendances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "group_id", "attendance_date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Attendance date is required")
    @Column(name = "attendance_date", nullable = false)
    private LocalDate date;
    
    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @NotNull(message = "Group is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status = AttendanceStatus.PRESENT;
    
    public enum AttendanceStatus {
        PRESENT,
        ABSENT
    }
}