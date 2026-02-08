package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.PaymentRequest;
import com.ogabek.CreativeLearningCenter.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse getById(Long id);

    List<PaymentResponse> getAll();

    List<PaymentResponse> getByStudentId(Long studentId);

    List<PaymentResponse> getByGroupId(Long groupId);
    
    List<PaymentResponse> getByGroupIdAndMonth(Long groupId, Integer year, Integer month);
    
    PaymentResponse update(Long id, PaymentRequest request);
    
    void delete(Long id);
}