package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// Response DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private Long id;
    private String fullName;
    private String parentName;
    private String parentPhoneNumber;
    private Long telegramChatId;
    private Boolean telegramLinked;
    private String telegramLinkCode;
    private Long activeGroupId;
    private BigDecimal totalPaid;
}

