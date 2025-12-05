package com.example.CreativeLearningCenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCreateRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone must be +998XXXXXXXXX")
    private String phoneNumber;
}
