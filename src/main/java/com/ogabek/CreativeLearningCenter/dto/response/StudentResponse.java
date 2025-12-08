package com.ogabek.CreativeLearningCenter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    
    private Long id;
    private String fullName;
    private String parentName;
    private String parentPhoneNumber;
    private Boolean smsLinked;
    private String smsLinkCode;
    private BigDecimal totalPaid;
    private Long activeGroupId;
    private String activeGroupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}