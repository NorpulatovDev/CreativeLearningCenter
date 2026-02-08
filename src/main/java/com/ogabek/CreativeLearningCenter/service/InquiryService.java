package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.InquiryRequest;
import com.ogabek.CreativeLearningCenter.dto.response.InquiryResponse;
import com.ogabek.CreativeLearningCenter.entity.InquiryStatus;

import java.util.List;

public interface InquiryService {

    InquiryResponse create(InquiryRequest request);

    InquiryResponse getById(Long id);

    List<InquiryResponse> getAll();

    List<InquiryResponse> getByStatus(InquiryStatus status);

    InquiryResponse update(Long id, InquiryRequest request);

    void delete(Long id);
}