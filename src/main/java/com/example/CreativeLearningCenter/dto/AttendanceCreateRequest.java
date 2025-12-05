package com.example.CreativeLearningCenter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreateRequest {
    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private List<AttendanceMarkRequest> absents;  // Only ABSENT students listed
}
