package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.StudentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    StudentResponse getById(Long id);

    List<StudentResponse> getAll();

    List<StudentResponse> getByGroupId(Long groupId, Integer year, Integer month);

    StudentResponse update(Long id, StudentRequest request);
    
    void delete(Long id);
}