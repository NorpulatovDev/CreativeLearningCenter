package com.example.CreativeLearningCenter.repository;

import com.example.CreativeLearningCenter.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByParentPhoneNumber(String phoneNumber);
    Optional<Student> findByTelegramLinkCode(String code);
    List<Student> findByActiveGroupId(Long groupId);
    boolean existsByParentPhoneNumber(String phoneNumber);
}