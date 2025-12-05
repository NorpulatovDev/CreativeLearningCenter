package com.example.CreativeLearningCenter.repository;

import com.example.CreativeLearningCenter.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByTeacherId(Long teacherId);
    List<Group> findAll();
}