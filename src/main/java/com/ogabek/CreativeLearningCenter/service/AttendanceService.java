package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.AttendanceRequest;
import com.ogabek.CreativeLearningCenter.dto.request.AttendanceUpdateRequest;
import com.ogabek.CreativeLearningCenter.dto.response.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<AttendanceResponse> createForGroup(AttendanceRequest request);

    AttendanceResponse getById(Long id);

    List<AttendanceResponse> getByGroupAndDate(Long groupId, LocalDate date);

    List<AttendanceResponse> getByMonth(Integer year, Integer month);

    List<AttendanceResponse> getByGroupIdAndMonth(Long groupId, Integer year, Integer month);

    List<AttendanceResponse> getByStudentIdAndMonth(Long studentId, Integer year, Integer month);

    List<AttendanceResponse> getByStudentGroupAndMonth(
            Long studentId, Long groupId, Integer year, Integer month);

    AttendanceResponse update(Long id, AttendanceUpdateRequest request);
}
