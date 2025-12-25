package com.ogabek.CreativeLearningCenter.service.impl;

import com.ogabek.CreativeLearningCenter.dto.response.ReportResponse.*;
import com.ogabek.CreativeLearningCenter.entity.*;
import com.ogabek.CreativeLearningCenter.repository.*;
import com.ogabek.CreativeLearningCenter.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final TeacherRepository teacherRepository;

    // Helper method to safely get teacher name
    private String getTeacherName(Group group) {
        try {
            if (group != null && group.getTeacher() != null) {
                return group.getTeacher().getFullName();
            }
        } catch (Exception e) {
            log.warn("Error getting teacher name: {}", e.getMessage());
        }
        return "Noma'lum";
    }

    // Helper to safely get list or empty
    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public DailyReport getDailyReport(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        log.info("Generating daily report for {}", date);

        try {
            List<Attendance> attendances = safeList(attendanceRepository.findByDate(date));
            List<Payment> payments = safeList(paymentRepository.findByPaidAtDate(date));

            int totalPresent = (int) attendances.stream()
                    .filter(a -> a != null && a.getStatus() == AttendanceStatus.PRESENT)
                    .count();
            int totalAbsent = (int) attendances.stream()
                    .filter(a -> a != null && a.getStatus() == AttendanceStatus.ABSENT)
                    .count();

            BigDecimal totalPaymentsAmount = payments.stream()
                    .filter(p -> p != null && p.getAmount() != null)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Group attendances by group
            Map<Long, List<Attendance>> attendancesByGroup = attendances.stream()
                    .filter(a -> a != null && a.getGroup() != null && a.getGroup().getId() != null)
                    .collect(Collectors.groupingBy(a -> a.getGroup().getId()));

            List<GroupAttendanceSummary> groupSummaries = new ArrayList<>();
            for (Map.Entry<Long, List<Attendance>> entry : attendancesByGroup.entrySet()) {
                try {
                    List<Attendance> groupAttendances = entry.getValue();
                    if (groupAttendances.isEmpty()) continue;

                    Attendance first = groupAttendances.get(0);
                    if (first.getGroup() == null) continue;

                    int present = (int) groupAttendances.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                            .count();
                    int absent = (int) groupAttendances.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                            .count();

                    groupSummaries.add(GroupAttendanceSummary.builder()
                            .groupId(first.getGroup().getId())
                            .groupName(first.getGroup().getName() != null ? first.getGroup().getName() : "")
                            .teacherName(getTeacherName(first.getGroup()))
                            .presentCount(present)
                            .absentCount(absent)
                            .totalStudents(present + absent)
                            .build());
                } catch (Exception e) {
                    log.warn("Error processing group attendance: {}", e.getMessage());
                }
            }

            groupSummaries.sort(Comparator.comparing(GroupAttendanceSummary::getGroupName));

            List<PaymentSummary> paymentSummaries = new ArrayList<>();
            for (Payment p : payments) {
                try {
                    if (p != null && p.getStudent() != null && p.getGroup() != null) {
                        paymentSummaries.add(PaymentSummary.builder()
                                .paymentId(p.getId())
                                .studentName(p.getStudent().getFullName() != null ? p.getStudent().getFullName() : "")
                                .groupName(p.getGroup().getName() != null ? p.getGroup().getName() : "")
                                .amount(p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                                .paidForMonth(p.getPaidForMonth() != null ? p.getPaidForMonth() : "")
                                .build());
                    }
                } catch (Exception e) {
                    log.warn("Error processing payment summary: {}", e.getMessage());
                }
            }

            return DailyReport.builder()
                    .date(date)
                    .totalStudentsPresent(totalPresent)
                    .totalStudentsAbsent(totalAbsent)
                    .totalPaymentsReceived(totalPaymentsAmount)
                    .paymentCount(payments.size())
                    .groupAttendances(groupSummaries)
                    .payments(paymentSummaries)
                    .build();

        } catch (Exception e) {
            log.error("Error generating daily report: {}", e.getMessage(), e);
            // Return empty report instead of throwing exception
            return DailyReport.builder()
                    .date(date)
                    .totalStudentsPresent(0)
                    .totalStudentsAbsent(0)
                    .totalPaymentsReceived(BigDecimal.ZERO)
                    .paymentCount(0)
                    .groupAttendances(new ArrayList<>())
                    .payments(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public MonthlyReport getMonthlyReport(int year, int month) {
        String monthKey = year + "-" + String.format("%02d", month);
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        log.info("Generating monthly report for {} {}", monthName, year);

        try {
            List<Group> allGroups = safeList(groupRepository.findAll());
            List<Payment> monthPayments = safeList(paymentRepository.findByPaidForMonth(monthKey));

            List<GroupMonthlyStats> groupStats = new ArrayList<>();
            Set<Long> studentsWhoPaid = new HashSet<>();
            Set<Long> studentsWhoDidNotPay = new HashSet<>();

            BigDecimal totalExpected = BigDecimal.ZERO;
            BigDecimal totalActual = BigDecimal.ZERO;

            for (Group group : allGroups) {
                try {
                    if (group == null || group.getId() == null) continue;

                    List<StudentGroup> activeEnrollments = safeList(
                            studentGroupRepository.findByGroupIdAndActiveTrue(group.getId()));

                    int activeStudents = activeEnrollments.size();
                    if (activeStudents == 0) continue;

                    BigDecimal groupFee = group.getMonthlyFee() != null ? group.getMonthlyFee() : BigDecimal.ZERO;
                    BigDecimal expectedForGroup = groupFee.multiply(BigDecimal.valueOf(activeStudents));
                    totalExpected = totalExpected.add(expectedForGroup);

                    // Find payments for this group in this month
                    List<Payment> groupPayments = monthPayments.stream()
                            .filter(p -> p != null && p.getGroup() != null &&
                                    group.getId().equals(p.getGroup().getId()))
                            .toList();

                    BigDecimal actualForGroup = groupPayments.stream()
                            .filter(p -> p.getAmount() != null)
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    totalActual = totalActual.add(actualForGroup);

                    Set<Long> paidStudentIds = groupPayments.stream()
                            .filter(p -> p.getStudent() != null && p.getStudent().getId() != null)
                            .map(p -> p.getStudent().getId())
                            .collect(Collectors.toSet());

                    int paidCount = paidStudentIds.size();
                    int unpaidCount = activeStudents - paidCount;

                    studentsWhoPaid.addAll(paidStudentIds);

                    // Track unpaid students
                    for (StudentGroup enrollment : activeEnrollments) {
                        try {
                            if (enrollment != null && enrollment.getStudent() != null &&
                                    enrollment.getStudent().getId() != null &&
                                    !paidStudentIds.contains(enrollment.getStudent().getId())) {
                                studentsWhoDidNotPay.add(enrollment.getStudent().getId());
                            }
                        } catch (Exception e) {
                            log.warn("Error tracking unpaid student: {}", e.getMessage());
                        }
                    }

                    BigDecimal collectionRate = BigDecimal.ZERO;
                    if (expectedForGroup.compareTo(BigDecimal.ZERO) > 0) {
                        collectionRate = actualForGroup.multiply(BigDecimal.valueOf(100))
                                .divide(expectedForGroup, 2, RoundingMode.HALF_UP);
                    }

                    groupStats.add(GroupMonthlyStats.builder()
                            .groupId(group.getId())
                            .groupName(group.getName() != null ? group.getName() : "")
                            .teacherName(getTeacherName(group))
                            .activeStudents(activeStudents)
                            .expectedRevenue(expectedForGroup)
                            .actualRevenue(actualForGroup)
                            .paidStudents(paidCount)
                            .unpaidStudents(unpaidCount)
                            .collectionRate(collectionRate)
                            .build());

                } catch (Exception e) {
                    log.warn("Error processing group stats for group: {}", e.getMessage());
                }
            }

            // Build unpaid students list
            List<StudentPaymentStatus> unpaidStudentsList = new ArrayList<>();
            for (Group group : allGroups) {
                try {
                    if (group == null || group.getId() == null) continue;

                    List<StudentGroup> activeEnrollments = safeList(
                            studentGroupRepository.findByGroupIdAndActiveTrue(group.getId()));

                    Set<Long> paidForGroup = monthPayments.stream()
                            .filter(p -> p != null && p.getGroup() != null &&
                                    group.getId().equals(p.getGroup().getId()) &&
                                    p.getStudent() != null && p.getStudent().getId() != null)
                            .map(p -> p.getStudent().getId())
                            .collect(Collectors.toSet());

                    for (StudentGroup enrollment : activeEnrollments) {
                        try {
                            Student student = enrollment != null ? enrollment.getStudent() : null;
                            if (student != null && student.getId() != null &&
                                    !paidForGroup.contains(student.getId())) {

                                BigDecimal fee = group.getMonthlyFee() != null ?
                                        group.getMonthlyFee() : BigDecimal.ZERO;

                                unpaidStudentsList.add(StudentPaymentStatus.builder()
                                        .studentId(student.getId())
                                        .studentName(student.getFullName() != null ? student.getFullName() : "")
                                        .parentName(student.getParentName() != null ? student.getParentName() : "")
                                        .parentPhoneNumber(student.getParentPhoneNumber() != null ?
                                                student.getParentPhoneNumber() : "")
                                        .groupId(group.getId())
                                        .groupName(group.getName() != null ? group.getName() : "")
                                        .amountDue(fee)
                                        .hasPaid(false)
                                        .build());
                            }
                        } catch (Exception e) {
                            log.warn("Error processing unpaid student: {}", e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error building unpaid list for group: {}", e.getMessage());
                }
            }

            // Attendance stats
            int presentCount = 0;
            int absentCount = 0;
            try {
                List<Attendance> monthAttendances = safeList(attendanceRepository.findByMonth(year, month));
                presentCount = (int) monthAttendances.stream()
                        .filter(a -> a != null && a.getStatus() == AttendanceStatus.PRESENT)
                        .count();
                absentCount = (int) monthAttendances.stream()
                        .filter(a -> a != null && a.getStatus() == AttendanceStatus.ABSENT)
                        .count();
            } catch (Exception e) {
                log.warn("Error getting attendance stats: {}", e.getMessage());
            }

            BigDecimal attendanceRate = BigDecimal.ZERO;
            if ((presentCount + absentCount) > 0) {
                attendanceRate = BigDecimal.valueOf(presentCount * 100.0 / (presentCount + absentCount))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal overallCollectionRate = BigDecimal.ZERO;
            if (totalExpected.compareTo(BigDecimal.ZERO) > 0) {
                overallCollectionRate = totalActual.multiply(BigDecimal.valueOf(100))
                        .divide(totalExpected, 2, RoundingMode.HALF_UP);
            }

            int totalActiveStudents = 0;
            try {
                List<StudentGroup> allEnrollments = safeList(studentGroupRepository.findAll());
                totalActiveStudents = (int) allEnrollments.stream()
                        .filter(sg -> sg != null && Boolean.TRUE.equals(sg.getActive()) &&
                                sg.getStudent() != null && sg.getStudent().getId() != null)
                        .map(sg -> sg.getStudent().getId())
                        .distinct()
                        .count();
            } catch (Exception e) {
                log.warn("Error counting active students: {}", e.getMessage());
            }

            return MonthlyReport.builder()
                    .year(year)
                    .month(month)
                    .monthName(monthName)
                    .totalActiveStudents(totalActiveStudents)
                    .totalGroups(allGroups.size())
                    .expectedRevenue(totalExpected)
                    .actualRevenue(totalActual)
                    .collectionRate(overallCollectionRate)
                    .totalPayments(monthPayments.size())
                    .studentsWhoPaid(studentsWhoPaid.size())
                    .studentsWhoDidNotPay(studentsWhoDidNotPay.size())
                    .groupStats(groupStats)
                    .unpaidStudents(unpaidStudentsList)
                    .attendanceStats(AttendanceStats.builder()
                            .totalPresent(presentCount)
                            .totalAbsent(absentCount)
                            .attendanceRate(attendanceRate)
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("Error generating monthly report: {}", e.getMessage(), e);
            // Return empty report
            return MonthlyReport.builder()
                    .year(year)
                    .month(month)
                    .monthName(monthName)
                    .totalActiveStudents(0)
                    .totalGroups(0)
                    .expectedRevenue(BigDecimal.ZERO)
                    .actualRevenue(BigDecimal.ZERO)
                    .collectionRate(BigDecimal.ZERO)
                    .totalPayments(0)
                    .studentsWhoPaid(0)
                    .studentsWhoDidNotPay(0)
                    .groupStats(new ArrayList<>())
                    .unpaidStudents(new ArrayList<>())
                    .attendanceStats(AttendanceStats.builder()
                            .totalPresent(0)
                            .totalAbsent(0)
                            .attendanceRate(BigDecimal.ZERO)
                            .build())
                    .build();
        }
    }

    @Override
    public YearlyReport getYearlyReport(int year) {
        log.info("Generating yearly report for {}", year);

        try {
            List<Payment> yearPayments = safeList(paymentRepository.findByYear(year));

            BigDecimal totalRevenue = yearPayments.stream()
                    .filter(p -> p != null && p.getAmount() != null)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Monthly breakdown
            List<MonthlyRevenueSummary> monthlyBreakdown = new ArrayList<>();
            for (int m = 1; m <= 12; m++) {
                try {
                    String monthKey = year + "-" + String.format("%02d", m);
                    final int currentMonth = m;

                    List<Payment> monthPayments = yearPayments.stream()
                            .filter(p -> p != null && monthKey.equals(p.getPaidForMonth()))
                            .toList();

                    BigDecimal monthRevenue = monthPayments.stream()
                            .filter(p -> p.getAmount() != null)
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    monthlyBreakdown.add(MonthlyRevenueSummary.builder()
                            .month(currentMonth)
                            .monthName(Month.of(currentMonth).getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                            .revenue(monthRevenue)
                            .paymentCount(monthPayments.size())
                            .build());
                } catch (Exception e) {
                    log.warn("Error processing month {}: {}", m, e.getMessage());
                }
            }

            // Teacher stats
            List<TeacherYearlyStats> teacherStats = new ArrayList<>();
            try {
                List<Teacher> teachers = safeList(teacherRepository.findAll());

                for (Teacher teacher : teachers) {
                    try {
                        if (teacher == null || teacher.getId() == null) continue;

                        List<Group> teacherGroups = safeList(groupRepository.findByTeacherId(teacher.getId()));

                        int totalStudents = 0;
                        for (Group g : teacherGroups) {
                            try {
                                if (g != null && g.getId() != null) {
                                    Integer count = studentGroupRepository.countActiveByGroupId(g.getId());
                                    totalStudents += (count != null ? count : 0);
                                }
                            } catch (Exception e) {
                                log.warn("Error counting students for group: {}", e.getMessage());
                            }
                        }

                        BigDecimal teacherRevenue = yearPayments.stream()
                                .filter(p -> p != null && p.getGroup() != null && p.getAmount() != null &&
                                        teacherGroups.stream().anyMatch(g -> g != null &&
                                                g.getId() != null && g.getId().equals(p.getGroup().getId())))
                                .map(Payment::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        teacherStats.add(TeacherYearlyStats.builder()
                                .teacherId(teacher.getId())
                                .teacherName(teacher.getFullName() != null ? teacher.getFullName() : "")
                                .groupCount(teacherGroups.size())
                                .totalStudents(totalStudents)
                                .totalRevenue(teacherRevenue)
                                .build());
                    } catch (Exception e) {
                        log.warn("Error processing teacher stats: {}", e.getMessage());
                    }
                }

                teacherStats.sort((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()));
            } catch (Exception e) {
                log.warn("Error getting teacher stats: {}", e.getMessage());
            }

            // Top groups
            List<GroupYearlyStats> topGroups = new ArrayList<>();
            try {
                List<Group> allGroups = safeList(groupRepository.findAll());

                for (Group group : allGroups) {
                    try {
                        if (group == null || group.getId() == null) continue;

                        List<Payment> groupPayments = yearPayments.stream()
                                .filter(p -> p != null && p.getGroup() != null &&
                                        group.getId().equals(p.getGroup().getId()))
                                .toList();

                        BigDecimal groupRevenue = groupPayments.stream()
                                .filter(p -> p.getAmount() != null)
                                .map(Payment::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        topGroups.add(GroupYearlyStats.builder()
                                .groupId(group.getId())
                                .groupName(group.getName() != null ? group.getName() : "")
                                .teacherName(getTeacherName(group))
                                .totalRevenue(groupRevenue)
                                .totalPayments(groupPayments.size())
                                .build());
                    } catch (Exception e) {
                        log.warn("Error processing group stats: {}", e.getMessage());
                    }
                }

                topGroups.sort((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()));
                if (topGroups.size() > 10) {
                    topGroups = new ArrayList<>(topGroups.subList(0, 10));
                }
            } catch (Exception e) {
                log.warn("Error getting top groups: {}", e.getMessage());
            }

            // Attendance stats
            int totalPresent = 0;
            int totalAbsent = 0;
            for (int m = 1; m <= 12; m++) {
                try {
                    List<Attendance> monthAttendances = safeList(attendanceRepository.findByMonth(year, m));
                    totalPresent += (int) monthAttendances.stream()
                            .filter(a -> a != null && a.getStatus() == AttendanceStatus.PRESENT)
                            .count();
                    totalAbsent += (int) monthAttendances.stream()
                            .filter(a -> a != null && a.getStatus() == AttendanceStatus.ABSENT)
                            .count();
                } catch (Exception e) {
                    log.warn("Error getting attendance for month {}: {}", m, e.getMessage());
                }
            }

            BigDecimal attendanceRate = BigDecimal.ZERO;
            if ((totalPresent + totalAbsent) > 0) {
                attendanceRate = BigDecimal.valueOf(totalPresent * 100.0 / (totalPresent + totalAbsent))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            return YearlyReport.builder()
                    .year(year)
                    .totalRevenue(totalRevenue)
                    .totalPayments(yearPayments.size())
                    .monthlyBreakdown(monthlyBreakdown)
                    .teacherStats(teacherStats)
                    .topGroups(topGroups)
                    .attendanceStats(AttendanceStats.builder()
                            .totalPresent(totalPresent)
                            .totalAbsent(totalAbsent)
                            .attendanceRate(attendanceRate)
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("Error generating yearly report: {}", e.getMessage(), e);
            // Return empty report
            return YearlyReport.builder()
                    .year(year)
                    .totalRevenue(BigDecimal.ZERO)
                    .totalPayments(0)
                    .monthlyBreakdown(new ArrayList<>())
                    .teacherStats(new ArrayList<>())
                    .topGroups(new ArrayList<>())
                    .attendanceStats(AttendanceStats.builder()
                            .totalPresent(0)
                            .totalAbsent(0)
                            .attendanceRate(BigDecimal.ZERO)
                            .build())
                    .build();
        }
    }
}