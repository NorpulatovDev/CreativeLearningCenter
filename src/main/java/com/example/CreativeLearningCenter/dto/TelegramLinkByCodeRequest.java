package com.example.CreativeLearningCenter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramLinkByCodeRequest {
    @NotNull(message = "Code is required")
    private String code;

    @NotNull(message = "Chat ID is required")
    private Long chatId;
}
