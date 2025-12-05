package com.example.CreativeLearningCenter.repository;

import com.example.CreativeLearningCenter.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByGroupIdAndDate(Long groupId, LocalDate date);
    List<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);
    List<Attendance> findByGroupId(Long groupId);
    List<Attendance> findByStudentId(Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.group.id = :groupId " +
           "AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findByGroupAndMonth(@Param("groupId") Long groupId, 
                                         @Param("year") int year, 
                                         @Param("month") int month);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId " +
           "AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findByStudentAndMonth(@Param("studentId") Long studentId,
                                           @Param("year") int year,
                                           @Param("month") int month);
    
    Optional<Attendance> findByStudentIdAndGroupIdAndDate(Long studentId, Long groupId, LocalDate date);
}