package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentTelegramLinkDto {
    private Long id;
    private String parentPhoneNumber;
    private Boolean telegramLinked;
    private Long telegramChatId;
    private String telegramLinkCode;
}
