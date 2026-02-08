package com.ogabek.CreativeLearningCenter.dto.response;

import com.ogabek.CreativeLearningCenter.entity.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponse {
    
    private Long id;
    private String fullName;
    private String parentName;
    private String parentPhoneNumber;
    private String interestedCourses;
    private InquiryStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}