package com.ogabek.CreativeLearningCenter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsLinkResponse {
    
    private Long id;
    private String fullName;
    private String smsLinkCode;
    private Boolean smsLinked;
    private String parentPhoneNumber;
}