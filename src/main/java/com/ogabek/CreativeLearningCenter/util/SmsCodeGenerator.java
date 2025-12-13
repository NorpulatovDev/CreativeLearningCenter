package com.ogabek.CreativeLearningCenter.util;

import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class SmsCodeGenerator {

    private static final String PREFIX = "STU-";
    private static final int MAX_ATTEMPTS = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StudentRepository studentRepository;

    /**
     * Generates a unique SMS link code in format: STU-XXXXX
     * where XXXXX is a random 5-digit number.
     * Uses synchronized block to prevent race conditions.
     */
    @Transactional
    public synchronized String generateUniqueSmsLinkCode() {
        String code;
        int attempts = 0;
        do {
            if (attempts++ >= MAX_ATTEMPTS) {
                // Fallback to timestamp-based code if too many collisions
                code = PREFIX + System.currentTimeMillis() % 100000;
            } else {
                code = generateCode();
            }
        } while (studentRepository.existsBySmsLinkCode(code));
        return code;
    }

    private String generateCode() {
        int number = RANDOM.nextInt(90000) + 10000; // 10000 to 99999
        return PREFIX + number;
    }
}