package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    List<Payment> findByGroupId(Long groupId);

    List<Payment> findByStudentIdAndGroupId(Long studentId, Long groupId);

    List<Payment> findByPaidForMonth(String paidForMonth);

    @Query("SELECT p FROM Payment p WHERE FUNCTION('DATE', p.paidAt) = :date")
    List<Payment> findByPaidAtDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Payment p WHERE p.paidForMonth LIKE :yearPrefix%")
    List<Payment> findByPaidForMonthStartingWith(@Param("yearPrefix") String yearPrefix);

    @Query("SELECT p FROM Payment p WHERE FUNCTION('YEAR', p.paidAt) = :year")
    List<Payment> findByYear(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId")
    BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.group.id = :groupId")
    BigDecimal getTotalPaidByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.id = :studentId AND p.group.id = :groupId AND p.paidForMonth = :month")
    BigDecimal getTotalPaidByStudentIdAndGroupIdAndMonth(@Param("studentId") Long studentId, 
                                                          @Param("groupId") Long groupId, 
                                                          @Param("month") String month);

    boolean existsByStudentIdAndGroupIdAndPaidForMonth(Long studentId, Long groupId, String paidForMonth);

    void deleteByGroupId(Long groupId);
}
