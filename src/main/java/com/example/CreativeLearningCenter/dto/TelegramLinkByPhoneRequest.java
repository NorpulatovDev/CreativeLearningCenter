package com.example.CreativeLearningCenter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramLinkByPhoneRequest {
    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Chat ID is required")
    private Long chatId;
}
