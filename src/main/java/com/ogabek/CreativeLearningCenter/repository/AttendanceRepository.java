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

    // Find all attendances for a student (needed for cascade deletion)
    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByGroupIdAndDate(Long groupId, LocalDate date);

    List<Attendance> findByDate(LocalDate date);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.group g LEFT JOIN FETCH g.teacher WHERE a.date = :date")
    List<Attendance> findByDateWithGroupAndTeacher(@Param("date") LocalDate date);

    Optional<Attendance> findByStudentIdAndGroupIdAndDate(Long studentId, Long groupId, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.group.id = :groupId " +
            "AND a.date >= :startDate AND a.date < :endDate")
    List<Attendance> findByGroupIdAndDateBetween(@Param("groupId") Long groupId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

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

    default List<Attendance> findByStudentIdAndGroupIdAndMonth(Long studentId, Long groupId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        return findByStudentIdAndGroupIdAndDateBetween(studentId, groupId, startDate, endDate);
    }

    @Query("SELECT a FROM Attendance a WHERE a.date >= :startDate AND a.date < :endDate")
    List<Attendance> findByDateBetween(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    default List<Attendance> findByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        return findByDateBetween(startDate, endDate);
    }

    void deleteByGroupId(Long groupId);

    boolean existsByGroupIdAndDate(Long groupId, LocalDate date);
}