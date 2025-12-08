package com.ogabek.CreativeLearningCenter.util;

import com.ogabek.CreativeLearningCenter.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class SmsCodeGenerator {
    
    private static final String PREFIX = "STU-";
    private static final int CODE_LENGTH = 5;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    private final StudentRepository studentRepository;
    
    /**
     * Generates a unique SMS link code in format: STU-XXXXX
     * where XXXXX is a random 5-digit number
     */
    public String generateUniqueSmsLinkCode() {
        String code;
        do {
            code = generateCode();
        } while (studentRepository.existsBySmsLinkCode(code));
        return code;
    }
    
    private String generateCode() {
        int number = RANDOM.nextInt(90000) + 10000; // 10000 to 99999
        return PREFIX + number;
    }
}