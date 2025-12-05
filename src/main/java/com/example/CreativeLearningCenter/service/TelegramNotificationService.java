package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.entity.Payment;
import com.example.CreativeLearningCenter.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TelegramNotificationService {

    private final TelegramBotService telegramBotService;

    public TelegramNotificationService(@Lazy TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    public void sendAbsenceNotification(Student student, LocalDate date) {
        if (!shouldSendNotification(student)) {
            return;
        }

        String message = String.format(
                "⚠️ Attendance Alert\n\n" +
                        "Student: %s\n" +
                        "Date: %s\n" +
                        "Status: ABSENT",
                student.getFullName(),
                date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );

        telegramBotService.sendMessage(student.getTelegramChatId(), message);
    }

    public void sendPaymentNotification(Student student, Payment payment) {
        if (!shouldSendNotification(student)) {
            return;
        }

        String message = String.format(
                "✅ Payment Received\n\n" +
                        "Student: %s\n" +
                        "Amount: %s UZS\n" +
                        "Month: %s\n" +
                        "Date: %s",
                student.getFullName(),
                payment.getAmount(),
                payment.getPaidForMonth(),
                payment.getPaidAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );

        telegramBotService.sendMessage(student.getTelegramChatId(), message);
    }

    private boolean shouldSendNotification(Student student) {
        return student.getTelegramLinked() && student.getTelegramChatId() != null;
    }
}