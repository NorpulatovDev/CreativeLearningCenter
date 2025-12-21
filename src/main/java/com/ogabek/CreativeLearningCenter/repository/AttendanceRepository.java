package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Attendance;
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

    List<Attendance> findByDate(LocalDate date);

    Optional<Attendance> findByStudentIdAndGroupIdAndDate(Long studentId, Long groupId, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.group.id = :groupId " +
            "AND FUNCTION('YEAR', a.date) = :year AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByGroupIdAndMonth(@Param("groupId") Long groupId,
                                           @Param("year") int year,
                                           @Param("month") int month);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId " +
            "AND FUNCTION('YEAR', a.date) = :year AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByStudentIdAndMonth(@Param("studentId") Long studentId,
                                             @Param("year") int year,
                                             @Param("month") int month);

    @Query("SELECT a FROM Attendance a WHERE " +
            "a.student.id = :studentId AND " +
            "a.group.id = :groupId AND " +
            "YEAR(a.date) = :year AND " +
            "MONTH(a.date) = :month " +
            "ORDER BY a.date DESC")
    List<Attendance> findByStudentIdAndGroupIdAndMonth(
            @Param("studentId") Long studentId,
            @Param("groupId") Long groupId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query("SELECT a FROM Attendance a WHERE " +
            "FUNCTION('YEAR', a.date) = :year AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date = :date")
    List<Attendance> findByStudentIdAndDate(@Param("studentId") Long studentId, @Param("date") LocalDate date);

    void deleteByGroupId(Long groupId);

    boolean existsByGroupIdAndDate(Long groupId, LocalDate date);
}
