package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    List<Payment> findByGroupId(Long groupId);

    List<Payment> findByStudentIdAndGroupId(Long studentId, Long groupId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId")
    BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.group.id = :groupId")
    BigDecimal getTotalPaidByGroupId(@Param("groupId") Long groupId);

    void deleteByGroupId(Long groupId);
}