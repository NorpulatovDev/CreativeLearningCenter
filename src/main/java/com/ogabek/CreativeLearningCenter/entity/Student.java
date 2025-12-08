package com.ogabek.CreativeLearningCenter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String parentName;

    @Column(nullable = false)
    private String parentPhoneNumber;

    @Column(nullable = false)
    @Builder.Default
    private Boolean smsLinked = false;

    @Column(nullable = false, unique = true)
    private String smsLinkCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_group_id")
    private Group activeGroup;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}