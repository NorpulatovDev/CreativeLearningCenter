package com.ogabek.CreativeLearningCenter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsLinkByCodeRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone number must be in format +998XXXXXXXXX")
    private String phoneNumber;
}