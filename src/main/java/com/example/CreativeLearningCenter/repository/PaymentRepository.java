package com.example.CreativeLearningCenter.repository;

import com.example.CreativeLearningCenter.entity.Payment;
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
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId")
    BigDecimal getTotalPaidByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.group.id = :groupId")
    BigDecimal getTotalPaidByGroup(@Param("groupId") Long groupId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.group.teacher.id = :teacherId")
    BigDecimal getTotalIncomeByTeacher(@Param("teacherId") Long teacherId);
}