package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findBySmsLinkCode(String smsLinkCode);

    List<Student> findByParentPhoneNumber(String parentPhoneNumber);

    boolean existsBySmsLinkCode(String smsLinkCode);

    // NEW METHOD - Fetch all students with their groups, groups' details, and teachers in one query
    @Query("SELECT DISTINCT s FROM Student s " +
            "LEFT JOIN FETCH s.studentGroups sg " +
            "LEFT JOIN FETCH sg.group g " +
            "LEFT JOIN FETCH g.teacher " +
            "ORDER BY s.id")
    List<Student> findAllWithGroups();
}