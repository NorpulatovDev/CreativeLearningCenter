package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.request.SmsLinkByCodeRequest;
import com.ogabek.CreativeLearningCenter.dto.request.SmsLinkByPhoneRequest;
import com.ogabek.CreativeLearningCenter.dto.response.SmsLinkResponse;
import com.ogabek.CreativeLearningCenter.service.SmsLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/link")
@RequiredArgsConstructor
public class SmsLinkController {
    
    private final SmsLinkService smsLinkService;
    
    /**
     * Link SMS notifications by phone number.
     * If multiple students have the same parent phone number, ALL of them will be linked.
     */
    @PostMapping("/by-phone")
    public ResponseEntity<List<SmsLinkResponse>> linkByPhone(@Valid @RequestBody SmsLinkByPhoneRequest request) {
        return ResponseEntity.ok(smsLinkService.linkByPhone(request));
    }
    
    /**
     * Link SMS notifications by SMS link code.
     * This will also update the parent phone number to the provided one.
     */
    @PostMapping("/by-code")
    public ResponseEntity<SmsLinkResponse> linkByCode(@Valid @RequestBody SmsLinkByCodeRequest request) {
        return ResponseEntity.ok(smsLinkService.linkByCode(request));
    }
    
    /**
     * Get SMS link status for a student.
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<SmsLinkResponse> getLinkStatus(@PathVariable Long studentId) {
        return ResponseEntity.ok(smsLinkService.getLinkStatus(studentId));
    }
}