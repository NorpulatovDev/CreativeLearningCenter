package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {

    List<StudentGroup> findByStudentId(Long studentId);

    List<StudentGroup> findByStudentIdAndActiveTrue(Long studentId);

    List<StudentGroup> findByGroupId(Long groupId);

    List<StudentGroup> findByGroupIdAndActiveTrue(Long groupId);

    Optional<StudentGroup> findByStudentIdAndGroupId(Long studentId, Long groupId);

    boolean existsByStudentIdAndGroupIdAndActiveTrue(Long studentId, Long groupId);

    @Query("SELECT COUNT(sg) FROM StudentGroup sg WHERE sg.group.id = :groupId AND sg.active = true")
    int countActiveByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT sg.student.id FROM StudentGroup sg WHERE sg.group.id = :groupId AND sg.active = true")
    List<Long> findActiveStudentIdsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT sg.student FROM StudentGroup sg WHERE sg.group.id = :groupId AND sg.active = true")
    List<Student> findActiveStudentsByGroupId(@Param("groupId") Long groupId);

    void deleteByGroupId(Long groupId);
}
