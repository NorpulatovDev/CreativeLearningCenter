package com.example.CreativeLearningCenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;
    
    @NotBlank(message = "Parent name is required")
    @Column(nullable = false)
    private String parentName;
    
    @NotBlank(message = "Parent phone number is required")
    @Column(nullable = false)
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone must be in format +998XXXXXXXXX")
    private String parentPhoneNumber;
    
    @Column(name = "telegram_chat_id")
    private Long telegramChatId;
    
    @Column(nullable = false)
    private Boolean telegramLinked = false;
    
    @Column(unique = true, nullable = false)
    private String telegramLinkCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group activeGroup;
    
    // Read-only: calculated from payments
    @Transient
    private BigDecimal totalPaid = BigDecimal.ZERO;
}