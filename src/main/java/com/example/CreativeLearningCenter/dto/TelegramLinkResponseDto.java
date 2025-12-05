package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramLinkResponseDto {
    private Long studentId;
    private String parentPhoneNumber;
    private Boolean telegramLinked;
    private Long telegramChatId;
    private String telegramLinkCode;
    private String message;
}