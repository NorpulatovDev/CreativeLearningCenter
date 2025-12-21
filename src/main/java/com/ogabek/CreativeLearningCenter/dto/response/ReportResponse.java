package com.ogabek.CreativeLearningCenter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReportResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyReport {
        private LocalDate date;
        private int totalStudentsPresent;
        private int totalStudentsAbsent;
        private BigDecimal totalPaymentsReceived;
        private int paymentCount;
        private List<GroupAttendanceSummary> groupAttendances;
        private List<PaymentSummary> payments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyReport {
        private int year;
        private int month;
        private String monthName;
        private int totalActiveStudents;
        private int totalGroups;
        private BigDecimal expectedRevenue;
        private BigDecimal actualRevenue;
        private BigDecimal collectionRate;
        private int totalPayments;
        private int studentsWhoPaid;
        private int studentsWhoDidNotPay;
        private List<GroupMonthlyStats> groupStats;
        private List<StudentPaymentStatus> unpaidStudents;
        private AttendanceStats attendanceStats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YearlyReport {
        private int year;
        private BigDecimal totalRevenue;
        private int totalPayments;
        private List<MonthlyRevenueSummary> monthlyBreakdown;
        private List<TeacherYearlyStats> teacherStats;
        private List<GroupYearlyStats> topGroups;
        private AttendanceStats attendanceStats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupAttendanceSummary {
        private Long groupId;
        private String groupName;
        private String teacherName;
        private int presentCount;
        private int absentCount;
        private int totalStudents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentSummary {
        private Long paymentId;
        private String studentName;
        private String groupName;
        private BigDecimal amount;
        private String paidForMonth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupMonthlyStats {
        private Long groupId;
        private String groupName;
        private String teacherName;
        private int activeStudents;
        private BigDecimal expectedRevenue;
        private BigDecimal actualRevenue;
        private int paidStudents;
        private int unpaidStudents;
        private BigDecimal collectionRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentPaymentStatus {
        private Long studentId;
        private String studentName;
        private String parentName;
        private String parentPhoneNumber;
        private Long groupId;
        private String groupName;
        private BigDecimal amountDue;
        private Boolean hasPaid;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyRevenueSummary {
        private int month;
        private String monthName;
        private BigDecimal revenue;
        private int paymentCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeacherYearlyStats {
        private Long teacherId;
        private String teacherName;
        private int groupCount;
        private int totalStudents;
        private BigDecimal totalRevenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupYearlyStats {
        private Long groupId;
        private String groupName;
        private String teacherName;
        private BigDecimal totalRevenue;
        private int totalPayments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceStats {
        private int totalPresent;
        private int totalAbsent;
        private BigDecimal attendanceRate;
    }
}
