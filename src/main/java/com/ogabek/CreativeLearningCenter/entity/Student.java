package com.ogabek.CreativeLearningCenter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(unique = true)
    private String smsLinkCode;

    // Many-to-many relationship through StudentGroup
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentGroup> studentGroups = new ArrayList<>();

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

    // Helper method to get active groups
    public List<Group> getActiveGroups() {
        return studentGroups.stream()
                .filter(StudentGroup::getActive)
                .map(StudentGroup::getGroup)
                .toList();
    }

    // Helper method to get active student groups
    public List<StudentGroup> getActiveStudentGroups() {
        return studentGroups.stream()
                .filter(StudentGroup::getActive)
                .toList();
    }
}
