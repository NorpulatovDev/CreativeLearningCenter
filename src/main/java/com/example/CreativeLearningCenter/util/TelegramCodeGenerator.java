package com.example.CreativeLearningCenter.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TelegramCodeGenerator {
    
    /**
     * Generates a random Telegram link code in format: STU-12820
     * @return generated code like "STU-12820"
     */
    public String generateCode() {
        String prefix = "STU";
        String randomPart = RandomStringUtils.randomNumeric(5);
        return prefix + "-" + randomPart;
    }
    
    /**
     * Validates if code is in correct format
     */
    public boolean isValidCodeFormat(String code) {
        return code != null && code.matches("^STU-\\d{5}$");
    }
}