package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.response.ReportResponse.*;
import com.ogabek.CreativeLearningCenter.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Daily, Monthly, and Yearly Reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily/{year}/{month}/{day}")
    @Operation(summary = "Get daily report", description = "Get attendance and payment summary for a specific day")
    public ResponseEntity<DailyReport> getDailyReport(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @PathVariable Integer day) {
        return ResponseEntity.ok(reportService.getDailyReport(year, month, day));
    }

    @GetMapping("/monthly/{year}/{month}")
    @Operation(summary = "Get monthly report", 
            description = "Get comprehensive monthly report including revenue, attendance, and unpaid students")
    public ResponseEntity<MonthlyReport> getMonthlyReport(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(reportService.getMonthlyReport(year, month));
    }

    @GetMapping("/yearly/{year}")
    @Operation(summary = "Get yearly report", 
            description = "Get yearly summary including revenue breakdown, teacher stats, and top performing groups")
    public ResponseEntity<YearlyReport> getYearlyReport(@PathVariable Integer year) {
        return ResponseEntity.ok(reportService.getYearlyReport(year));
    }
}
