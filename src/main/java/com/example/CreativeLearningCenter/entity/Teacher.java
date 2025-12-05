package com.example.CreativeLearningCenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;
    
    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone must be in format +998XXXXXXXXX")
    private String phoneNumber;
    
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Group> groups;
    
    // Read-only: calculated from groups
    @Transient
    private BigDecimal totalIncome = BigDecimal.ZERO;
}