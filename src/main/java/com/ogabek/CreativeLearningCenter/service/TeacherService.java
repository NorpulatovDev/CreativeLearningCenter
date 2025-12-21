package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.TeacherRequest;
import com.ogabek.CreativeLearningCenter.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {

    TeacherResponse create(TeacherRequest request);

    TeacherResponse getById(Long id);

    List<TeacherResponse> getAll();

    TeacherResponse update(Long id, TeacherRequest request);

    void delete(Long id);
}
