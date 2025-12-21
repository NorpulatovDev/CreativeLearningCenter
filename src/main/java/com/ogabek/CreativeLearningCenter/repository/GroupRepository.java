package com.ogabek.CreativeLearningCenter.repository;

import com.ogabek.CreativeLearningCenter.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByTeacherIdOrderByNameAsc(Long teacherId);

    List<Group> findAllByOrderByTeacherIdAscNameAsc();

    List<Group> findByTeacherId(Long teacherId);
}
