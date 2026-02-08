package com.ogabek.CreativeLearningCenter.mapper;

import com.ogabek.CreativeLearningCenter.dto.request.InquiryRequest;
import com.ogabek.CreativeLearningCenter.dto.response.InquiryResponse;
import com.ogabek.CreativeLearningCenter.entity.Inquiry;
import com.ogabek.CreativeLearningCenter.entity.InquiryStatus;
import org.springframework.stereotype.Component;

@Component
public class InquiryMapper {
    
    public Inquiry toEntity(InquiryRequest request) {
        return Inquiry.builder()
                .fullName(request.getFullName())
                .parentName(request.getParentName())
                .parentPhoneNumber(request.getParentPhoneNumber())
                .interestedCourses(request.getInterestedCourses())
                .status(request.getStatus() != null ? request.getStatus() : InquiryStatus.NEW)
                .notes(request.getNotes())
                .build();
    }
    
    public InquiryResponse toResponse(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .fullName(inquiry.getFullName())
                .parentName(inquiry.getParentName())
                .parentPhoneNumber(inquiry.getParentPhoneNumber())
                .interestedCourses(inquiry.getInterestedCourses())
                .status(inquiry.getStatus())
                .notes(inquiry.getNotes())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }
    
    public void updateEntity(Inquiry inquiry, InquiryRequest request) {
        inquiry.setFullName(request.getFullName());
        inquiry.setParentName(request.getParentName());
        inquiry.setParentPhoneNumber(request.getParentPhoneNumber());
        inquiry.setInterestedCourses(request.getInterestedCourses());
        if (request.getStatus() != null) {
            inquiry.setStatus(request.getStatus());
        }
        inquiry.setNotes(request.getNotes());
    }
}