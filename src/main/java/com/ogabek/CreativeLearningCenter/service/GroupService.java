package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.GroupRequest;
import com.ogabek.CreativeLearningCenter.dto.response.GroupResponse;

import java.util.List;

public interface GroupService {

    GroupResponse create(GroupRequest request);

    GroupResponse getById(Long id);

    List<GroupResponse> getAll();

    List<GroupResponse> getAllSortedByTeacher();

    List<GroupResponse> getByTeacherId(Long teacherId);

    GroupResponse update(Long id, GroupRequest request);

    void delete(Long id);
}
