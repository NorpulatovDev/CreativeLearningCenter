package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.entity.Attendance;
import com.example.CreativeLearningCenter.entity.Payment;
import com.example.CreativeLearningCenter.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TelegramNotificationService {
    
    private final WebClient webClient;
    private final String botToken;
    private final String apiUrl;
    
    public TelegramNotificationService(WebClient.Builder webClientBuilder,
                                       @Value("${telegram.api-token}") String botToken,
                                       @Value("${telegram.api-url}") String apiUrl) {
        this.botToken = botToken;
        this.apiUrl = apiUrl;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }
    
    /**
     * Sends absence notification to parent via Telegram
     */
    public void sendAbsenceNotification(Student student, LocalDate date) {
        if (!shouldSendNotification(student)) {
            log.debug("Telegram not linked for student: {}", student.getId());
            return;
        }
        
        String message = String.format(
            "⚠️ Attendance Alert\n\n" +
            "Student: %s\n" +
            "Date: %s\n" +
            "Status: ABSENT\n\n" +
            "Please contact the learning center if this is an error.",
            student.getFullName(),
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );
        
        sendMessage(student.getTelegramChatId(), message);
    }
    
    /**
     * Sends payment notification to parent via Telegram
     */
    public void sendPaymentNotification(Student student, Payment payment) {
        if (!shouldSendNotification(student)) {
            log.debug("Telegram not linked for student: {}", student.getId());
            return;
        }
        
        String message = String.format(
            "✅ Payment Received\n\n" +
            "Student: %s\n" +
            "Amount: %s UZS\n" +
            "Paid For: %s\n" +
            "Date: %s\n\n" +
            "Thank you for your payment!",
            student.getFullName(),
            payment.getAmount(),
            payment.getPaidForMonth(),
            payment.getPaidAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );
        
        sendMessage(student.getTelegramChatId(), message);
    }
    
    /**
     * Sends a generic message to Telegram user
     */
    private void sendMessage(Long chatId, String message) {
        try {
            String url = String.format("%s/bot%s/sendMessage", apiUrl, botToken);
            
            webClient.post()
                .uri(url)
                .bodyValue(new TelegramMessage(chatId, message))
                .retrieve()
                .toEntity(String.class)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                    response -> log.info("Telegram message sent successfully to chat: {}", chatId),
                    error -> log.error("Failed to send Telegram message to chat {}: {}", chatId, error.getMessage())
                );
        } catch (Exception e) {
            log.error("Error sending Telegram notification to chat {}: {}", chatId, e.getMessage());
        }
    }
    
    private boolean shouldSendNotification(Student student) {
        return student.getTelegramLinked() && student.getTelegramChatId() != null;
    }
    
    // Helper DTO for Telegram API
    private static class TelegramMessage {
        public Long chat_id;
        public String text;
        
        TelegramMessage(Long chatId, String text) {
            this.chat_id = chatId;
            this.text = text;
        }
    }
}