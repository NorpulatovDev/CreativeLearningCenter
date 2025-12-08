package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.SmsLinkByCodeRequest;
import com.ogabek.CreativeLearningCenter.dto.request.SmsLinkByPhoneRequest;
import com.ogabek.CreativeLearningCenter.dto.response.SmsLinkResponse;

import java.util.List;

public interface SmsLinkService {
    
    List<SmsLinkResponse> linkByPhone(SmsLinkByPhoneRequest request);
    
    SmsLinkResponse linkByCode(SmsLinkByCodeRequest request);
    
    SmsLinkResponse getLinkStatus(Long studentId);
}