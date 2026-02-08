package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.request.InquiryRequest;
import com.ogabek.CreativeLearningCenter.dto.response.InquiryResponse;
import com.ogabek.CreativeLearningCenter.entity.Inquiry;
import com.ogabek.CreativeLearningCenter.entity.InquiryStatus;
import com.ogabek.CreativeLearningCenter.exception.ResourceNotFoundException;
import com.ogabek.CreativeLearningCenter.mapper.InquiryMapper;
import com.ogabek.CreativeLearningCenter.repository.InquiryRepository;
import com.ogabek.CreativeLearningCenter.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;

    @Override
    public InquiryResponse create(InquiryRequest request) {
        log.info("Creating inquiry for: {}", request.getFullName());

        Inquiry inquiry = inquiryMapper.toEntity(request);
        inquiry = inquiryRepository.save(inquiry);

        log.info("Inquiry created with id: {}", inquiry.getId());
        return inquiryMapper.toResponse(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponse getById(Long id) {
        Inquiry inquiry = findInquiryById(id);
        return inquiryMapper.toResponse(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InquiryResponse> getAll() {
        return inquiryRepository.findAll().stream()
                .map(inquiryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InquiryResponse> getByStatus(InquiryStatus status) {
        return inquiryRepository.findByStatus(status).stream()
                .map(inquiryMapper::toResponse)
                .toList();
    }

    @Override
    public InquiryResponse update(Long id, InquiryRequest request) {
        log.info("Updating inquiry: {}", id);

        Inquiry inquiry = findInquiryById(id);
        inquiryMapper.updateEntity(inquiry, request);
        inquiry = inquiryRepository.save(inquiry);

        return inquiryMapper.toResponse(inquiry);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting inquiry: {}", id);

        Inquiry inquiry = findInquiryById(id);
        inquiryRepository.delete(inquiry);

        log.info("Inquiry deleted: {}", id);
    }

    private Inquiry findInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", id));
    }
}