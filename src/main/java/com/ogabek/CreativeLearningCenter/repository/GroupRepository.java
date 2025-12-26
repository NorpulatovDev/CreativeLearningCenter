package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByTeacherIdOrderByNameAsc(Long teacherId);

    List<Group> findAllByOrderByTeacherIdAscNameAsc();

    List<Group> findByTeacherId(Long teacherId);

    // Fetch groups with teachers eagerly loaded to avoid LazyInitializationException
    // Using LEFT JOIN FETCH to handle cases where teacher might be null
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.teacher")
    List<Group> findAllWithTeacher();

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.teacher WHERE g.teacher.id = :teacherId")
    List<Group> findByTeacherIdWithTeacher(@Param("teacherId") Long teacherId);
}