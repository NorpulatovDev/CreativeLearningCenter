package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.entity.Payment;
import com.ogabek.CreativeLearningCenter.entity.Student;
import com.ogabek.CreativeLearningCenter.service.SmsNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SmsNotificationServiceImpl implements SmsNotificationService {
    
    private final WebClient webClient;
    private final String apiToken;
    private final String senderName;
    
    public SmsNotificationServiceImpl(
            @Value("${eskiz.api.url}") String apiUrl,
            @Value("${eskiz.api.token}") String apiToken,
            @Value("${eskiz.sender.name}") String senderName) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
        this.apiToken = apiToken;
        this.senderName = senderName;
    }
    
    @Override
    public void sendAbsenceNotification(Student student, LocalDate date, String groupName) {
        if (!canSendSms(student)) {
            log.debug("SMS not sent - student {} is not linked or has no phone", student.getId());
            return;
        }
        
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String message = String.format(
                "Hurmatli %s! Farzandingiz %s bugun (%s) \"%s\" guruhidagi darsga kelmadi. Creative Learning Center",
                student.getParentName(),
                student.getFullName(),
                formattedDate,
                groupName
        );
        
        sendSms(student.getParentPhoneNumber(), message);
    }
    
    @Override
    public void sendPaymentNotification(Student student, Payment payment) {
        if (!canSendSms(student)) {
            log.debug("SMS not sent - student {} is not linked or has no phone", student.getId());
            return;
        }
        
        String message = String.format(
                "Hurmatli %s! %s uchun %s so'm to'lov qabul qilindi (%s oy uchun, \"%s\" guruhi). Rahmat! Creative Learning Center",
                student.getParentName(),
                student.getFullName(),
                payment.getAmount().toPlainString(),
                payment.getPaidForMonth(),
                payment.getGroup().getName()
        );
        
        sendSms(student.getParentPhoneNumber(), message);
    }
    
    private boolean canSendSms(Student student) {
        return Boolean.TRUE.equals(student.getSmsLinked()) 
                && student.getParentPhoneNumber() != null 
                && !student.getParentPhoneNumber().isBlank();
    }
    
    private void sendSms(String phoneNumber, String message) {
        // Remove + prefix if present for Eskiz API
        String normalizedPhone = phoneNumber.startsWith("+") 
                ? phoneNumber.substring(1) 
                : phoneNumber;
        
        log.info("Sending SMS to {}: {}", normalizedPhone, message);
        
        try {
            webClient.post()
                    .uri("/message/sms/send")
                    .header("Authorization", "Bearer " + apiToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters
                            .fromFormData("mobile_phone", normalizedPhone)
                            .with("message", message)
                            .with("from", senderName))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("SMS sent successfully to {}: {}", normalizedPhone, response))
                    .doOnError(error -> log.error("Failed to send SMS to {}: {}", normalizedPhone, error.getMessage()))
                    .subscribe();
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", normalizedPhone, e.getMessage());
        }
    }
}