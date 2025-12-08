package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.entity.Payment;
import com.ogabek.CreativeLearningCenter.entity.Student;

import java.time.LocalDate;

public interface SmsNotificationService {
    
    void sendAbsenceNotification(Student student, LocalDate date);
    
    void sendPaymentNotification(Student student, Payment payment);
}