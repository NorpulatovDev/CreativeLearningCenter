package com.example.CreativeLearningCenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Group name is required")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Teacher is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
    
    @NotNull(message = "Monthly fee is required")
    @Positive(message = "Monthly fee must be positive")
    @Column(nullable = false)
    private BigDecimal monthlyFee;
    
    @OneToMany(mappedBy = "activeGroup", fetch = FetchType.LAZY)
    private List<Student> students;
    
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Attendance> attendances;
    
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Read-only: calculated values
    @Transient
    private Long studentsCount = 0L;
    
    @Transient
    private BigDecimal totalAmountToPay = BigDecimal.ZERO;
    
    @Transient
    private BigDecimal totalPaid = BigDecimal.ZERO;
}