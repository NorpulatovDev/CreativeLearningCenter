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

    // Fetch attendance with group and teacher eagerly loaded for reports
    // Using LEFT JOIN FETCH to handle cases where teacher might be null
    @Query("SELECT a FROM Attendance a JOIN FETCH a.group g LEFT JOIN FETCH g.teacher WHERE a.date = :date")
    List<Attendance> findByDateWithGroupAndTeacher(@Param("date") LocalDate date);

    Optional<Attendance> findByStudentIdAndGroupIdAndDate(Long studentId, Long groupId, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.group.id = :groupId " +
            "AND a.date >= :startDate AND a.date < :endDate")
    List<Attendance> findByGroupIdAndDateBetween(@Param("groupId") Long groupId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // Keep old method signature but use date range internally
    default List<Attendance> findByGroupIdAndMonth(Long groupId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        return findByGroupIdAndDateBetween(groupId, startDate, endDate);
    }

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId " +
            "AND a.date >= :startDate AND a.date < :endDate")
    List<Attendance> findByStudentIdAndDateBetween(@Param("studentId") Long studentId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    // Keep old method signature but use date range internally
    default List<Attendance> findByStudentIdAndMonth(Long studentId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        return findByStudentIdAndDateBetween(studentId, startDate, endDate);
    }

    @Query("SELECT a FROM Attendance a WHERE " +
            "a.student.id = :studentId AND " +
            "a.group.id = :groupId AND " +
            "a.date >= :startDate AND a.date < :endDate " +
            "ORDER BY a.date DESC")
    List<Attendance> findByStudentIdAndGroupIdAndDateBetween(
            @Param("studentId") Long studentId,
            @Param("groupId") Long groupId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Keep old method signature
    default List<Attendance> findByStudentIdAndGroupIdAndMonth(Long studentId, Long groupId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        return findByStudentIdAndGroupIdAndDateBetween(studentId, groupId, startDate, endDate);
    }

    @Query("SELECT a FROM Attendance a WHERE a.date >= :startDate AND a.date < :endDate")
    List<Attendance> findByDateBetween(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // Keep old method signature but use date range internally
    default List<Attendance> findByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        return findByDateBetween(startDate, endDate);
    }

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date = :date")
    List<Attendance> findByStudentIdAndDate(@Param("studentId") Long studentId, @Param("date") LocalDate date);

    void deleteByGroupId(Long groupId);

    boolean existsByGroupIdAndDate(Long groupId, LocalDate date);
}