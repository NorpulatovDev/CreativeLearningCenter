package com.ogabek.CreativeLearningCenter.controller;

import com.ogabek.CreativeLearningCenter.dto.response.ReportResponse.*;
import com.ogabek.CreativeLearningCenter.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Daily, Monthly, and Yearly Reports")
public class ReportController {

    private final ReportService reportService;

    /* ===================== DAILY ===================== */

    @GetMapping("/daily/{year}/{month}/{day}")
    @Operation(summary = "Get daily report")
    public ResponseEntity<DailyReport> getDailyReport(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @PathVariable Integer day) {

        log.info("Daily report request: y={}, m={}, d={}", year, month, day);

        if (!isValidDate(year, month, day)) {
            return ResponseEntity.ok(emptyDaily(year, month, day));
        }

        return ResponseEntity.ok(
                reportService.getDailyReport(year, month, day)
        );
    }

    /* ===================== MONTHLY ===================== */

    @GetMapping("/monthly/{year}/{month}")
    @Operation(summary = "Get monthly report")
    public ResponseEntity<MonthlyReport> getMonthlyReport(
            @PathVariable Integer year,
            @PathVariable Integer month) {

        log.info("Monthly report request: y={}, m={}", year, month);

        if (month == null || month < 1 || month > 12) {
            return ResponseEntity.ok(emptyMonthly(year, month));
        }

        return ResponseEntity.ok(
                reportService.getMonthlyReport(year, month)
        );
    }

    /* ===================== YEARLY ===================== */

    @GetMapping("/yearly/{year}")
    @Operation(summary = "Get yearly report")
    public ResponseEntity<YearlyReport> getYearlyReport(
            @PathVariable Integer year) {

        log.info("Yearly report request: y={}", year);
        return ResponseEntity.ok(reportService.getYearlyReport(year));
    }

    /* ===================== HELPERS ===================== */

    private boolean isValidDate(Integer year, Integer month, Integer day) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* ===================== EMPTY RESPONSES ===================== */

    private DailyReport emptyDaily(Integer year, Integer month, Integer day) {
        LocalDate date;
        try {
            date = LocalDate.of(
                    year,
                    month != null && month > 0 ? month : 1,
                    day != null && day > 0 ? day : 1
            );
        } catch (Exception e) {
            date = LocalDate.now();
        }

        return DailyReport.builder()
                .date(date)
                .totalStudentsPresent(0)
                .totalStudentsAbsent(0)
                .totalPaymentsReceived(BigDecimal.ZERO)
                .paymentCount(0)
                .groupAttendances(List.of())
                .payments(List.of())
                .build();
    }

    private MonthlyReport emptyMonthly(Integer year, Integer month) {
        return MonthlyReport.builder()
                .year(year)
                .month(month)
                .monthName("Unknown")
                .totalActiveStudents(0)
                .totalGroups(0)
                .expectedRevenue(BigDecimal.ZERO)
                .actualRevenue(BigDecimal.ZERO)
                .collectionRate(BigDecimal.ZERO)
                .totalPayments(0)
                .studentsWhoPaid(0)
                .studentsWhoDidNotPay(0)
                .groupStats(List.of())
                .unpaidStudents(List.of())
                .attendanceStats(
                        AttendanceStats.builder()
                                .totalPresent(0)
                                .totalAbsent(0)
                                .attendanceRate(BigDecimal.ZERO)
                                .build()
                )
                .build();
    }
}
