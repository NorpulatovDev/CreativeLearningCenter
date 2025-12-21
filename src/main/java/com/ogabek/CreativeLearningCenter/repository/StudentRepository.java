package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findBySmsLinkCode(String smsLinkCode);

    List<Student> findByParentPhoneNumber(String parentPhoneNumber);

    boolean existsBySmsLinkCode(String smsLinkCode);
}
