package com.example.CreativeLearningCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest {
    private String fullName;
    private String parentName;
    private String parentPhoneNumber;
    private Long activeGroupId;
}
