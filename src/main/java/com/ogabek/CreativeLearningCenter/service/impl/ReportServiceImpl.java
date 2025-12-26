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

    @Override
    public DailyReport getDailyReport(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        log.info("Generating daily report for {}", date);

        List<Attendance> attendances = attendanceRepository.findByDateWithGroupAndTeacher(date);
        List<Payment> payments = paymentRepository.findByPaidAtDate(date);

        int totalPresent = (int) attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
        int totalAbsent = (int) attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        BigDecimal totalPayments = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group attendances by group
        Map<Long, List<Attendance>> attendancesByGroup = attendances.stream()
                .collect(Collectors.groupingBy(a -> a.getGroup().getId()));

        List<GroupAttendanceSummary> groupSummaries = attendancesByGroup.entrySet().stream()
                .map(entry -> {
                    List<Attendance> groupAttendances = entry.getValue();
                    Attendance first = groupAttendances.get(0);
                    int present = (int) groupAttendances.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                            .count();
                    int absent = (int) groupAttendances.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                            .count();

                    String teacherName = first.getGroup().getTeacher() != null
                            ? first.getGroup().getTeacher().getFullName()
                            : "Unassigned";

                    return GroupAttendanceSummary.builder()
                            .groupId(first.getGroup().getId())
                            .groupName(first.getGroup().getName())
                            .teacherName(teacherName)
                            .presentCount(present)
                            .absentCount(absent)
                            .totalStudents(present + absent)
                            .build();
                })
                .sorted(Comparator.comparing(GroupAttendanceSummary::getGroupName))
                .toList();

        List<PaymentSummary> paymentSummaries = payments.stream()
                .map(p -> PaymentSummary.builder()
                        .paymentId(p.getId())
                        .studentName(p.getStudent().getFullName())
                        .groupName(p.getGroup().getName())
                        .amount(p.getAmount())
                        .paidForMonth(p.getPaidForMonth())
                        .build())
                .toList();

        return DailyReport.builder()
                .date(date)
                .totalStudentsPresent(totalPresent)
                .totalStudentsAbsent(totalAbsent)
                .totalPaymentsReceived(totalPayments)
                .paymentCount(payments.size())
                .groupAttendances(groupSummaries)
                .payments(paymentSummaries)
                .build();
    }

    @Override
    public MonthlyReport getMonthlyReport(int year, int month) {
        String monthKey = year + "-" + String.format("%02d", month);
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        log.info("Generating monthly report for {} {}", monthName, year);

        List<Group> allGroups = groupRepository.findAllWithTeacher();
        List<Payment> monthPayments = paymentRepository.findByPaidForMonth(monthKey);

        // Calculate expected and actual revenue per group
        List<GroupMonthlyStats> groupStats = new ArrayList<>();
        Set<Long> studentsWhoPaid = new HashSet<>();
        Set<Long> studentsWhoDidNotPay = new HashSet<>();

        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;

        for (Group group : allGroups) {
            List<StudentGroup> activeEnrollments = studentGroupRepository.findByGroupIdAndActiveTrue(group.getId());
            int activeStudents = activeEnrollments.size();

            if (activeStudents == 0) continue;

            BigDecimal expectedForGroup = group.getMonthlyFee().multiply(BigDecimal.valueOf(activeStudents));
            totalExpected = totalExpected.add(expectedForGroup);

            // Find payments for this group in this month
            List<Payment> groupPayments = monthPayments.stream()
                    .filter(p -> p.getGroup().getId().equals(group.getId()))
                    .toList();

            BigDecimal actualForGroup = groupPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalActual = totalActual.add(actualForGroup);

            Set<Long> paidStudentIds = groupPayments.stream()
                    .map(p -> p.getStudent().getId())
                    .collect(Collectors.toSet());

            int paidCount = paidStudentIds.size();
            int unpaidCount = activeStudents - paidCount;

            studentsWhoPaid.addAll(paidStudentIds);

            // Track unpaid students
            for (StudentGroup enrollment : activeEnrollments) {
                if (!paidStudentIds.contains(enrollment.getStudent().getId())) {
                    studentsWhoDidNotPay.add(enrollment.getStudent().getId());
                }
            }

            BigDecimal collectionRate = expectedForGroup.compareTo(BigDecimal.ZERO) > 0
                    ? actualForGroup.multiply(BigDecimal.valueOf(100))
                    .divide(expectedForGroup, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            String teacherName = group.getTeacher() != null ? group.getTeacher().getFullName() : "Unassigned";

            groupStats.add(GroupMonthlyStats.builder()
                    .groupId(group.getId())
                    .groupName(group.getName())
                    .teacherName(teacherName)
                    .activeStudents(activeStudents)
                    .expectedRevenue(expectedForGroup)
                    .actualRevenue(actualForGroup)
                    .paidStudents(paidCount)
                    .unpaidStudents(unpaidCount)
                    .collectionRate(collectionRate)
                    .build());
        }

        // Build unpaid students list with details
        List<StudentPaymentStatus> unpaidStudentsList = new ArrayList<>();
        for (Group group : allGroups) {
            List<StudentGroup> activeEnrollments = studentGroupRepository.findByGroupIdAndActiveTrue(group.getId());
            Set<Long> paidForGroup = monthPayments.stream()
                    .filter(p -> p.getGroup().getId().equals(group.getId()))
                    .map(p -> p.getStudent().getId())
                    .collect(Collectors.toSet());

            for (StudentGroup enrollment : activeEnrollments) {
                Student student = enrollment.getStudent();
                if (!paidForGroup.contains(student.getId())) {
                    unpaidStudentsList.add(StudentPaymentStatus.builder()
                            .studentId(student.getId())
                            .studentName(student.getFullName())
                            .parentName(student.getParentName())
                            .parentPhoneNumber(student.getParentPhoneNumber())
                            .groupId(group.getId())
                            .groupName(group.getName())
                            .amountDue(group.getMonthlyFee())
                            .hasPaid(false)
                            .build());
                }
            }
        }

        // Attendance stats for the month
        List<Attendance> monthAttendances = attendanceRepository.findByMonth(year, month);
        int presentCount = (int) monthAttendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
        int absentCount = (int) monthAttendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        BigDecimal attendanceRate = (presentCount + absentCount) > 0
                ? BigDecimal.valueOf(presentCount * 100.0 / (presentCount + absentCount))
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal overallCollectionRate = totalExpected.compareTo(BigDecimal.ZERO) > 0
                ? totalActual.multiply(BigDecimal.valueOf(100))
                .divide(totalExpected, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int totalActiveStudents = studentGroupRepository.findAll().stream()
                .filter(StudentGroup::getActive)
                .map(sg -> sg.getStudent().getId())
                .collect(Collectors.toSet())
                .size();

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
    }

    @Override
    public YearlyReport getYearlyReport(int year) {
        log.info("Generating yearly report for {}", year);

        List<Payment> yearPayments = paymentRepository.findByYear(year);
        BigDecimal totalRevenue = yearPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Monthly breakdown
        List<MonthlyRevenueSummary> monthlyBreakdown = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String monthKey = year + "-" + String.format("%02d", month);
            List<Payment> monthPayments = yearPayments.stream()
                    .filter(p -> p.getPaidForMonth().equals(monthKey))
                    .toList();

            BigDecimal monthRevenue = monthPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyBreakdown.add(MonthlyRevenueSummary.builder()
                    .month(month)
                    .monthName(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .revenue(monthRevenue)
                    .paymentCount(monthPayments.size())
                    .build());
        }

        // Teacher stats
        List<Teacher> teachers = teacherRepository.findAll();
        List<TeacherYearlyStats> teacherStats = teachers.stream()
                .map(teacher -> {
                    List<Group> teacherGroups = groupRepository.findByTeacherIdWithTeacher(teacher.getId());
                    int totalStudents = teacherGroups.stream()
                            .mapToInt(g -> studentGroupRepository.countActiveByGroupId(g.getId()))
                            .sum();

                    BigDecimal teacherRevenue = yearPayments.stream()
                            .filter(p -> teacherGroups.stream()
                                    .anyMatch(g -> g.getId().equals(p.getGroup().getId())))
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return TeacherYearlyStats.builder()
                            .teacherId(teacher.getId())
                            .teacherName(teacher.getFullName())
                            .groupCount(teacherGroups.size())
                            .totalStudents(totalStudents)
                            .totalRevenue(teacherRevenue)
                            .build();
                })
                .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
                .toList();

        // Top groups by revenue
        List<Group> allGroups = groupRepository.findAllWithTeacher();
        List<GroupYearlyStats> topGroups = allGroups.stream()
                .map(group -> {
                    List<Payment> groupPayments = yearPayments.stream()
                            .filter(p -> p.getGroup().getId().equals(group.getId()))
                            .toList();

                    BigDecimal groupRevenue = groupPayments.stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    String teacherName = group.getTeacher() != null ? group.getTeacher().getFullName() : "Unassigned";

                    return GroupYearlyStats.builder()
                            .groupId(group.getId())
                            .groupName(group.getName())
                            .teacherName(teacherName)
                            .totalRevenue(groupRevenue)
                            .totalPayments(groupPayments.size())
                            .build();
                })
                .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
                .limit(10)
                .toList();

        // Yearly attendance stats
        int totalPresent = 0;
        int totalAbsent = 0;
        for (int month = 1; month <= 12; month++) {
            List<Attendance> monthAttendances = attendanceRepository.findByMonth(year, month);
            totalPresent += (int) monthAttendances.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                    .count();
            totalAbsent += (int) monthAttendances.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                    .count();
        }

        BigDecimal attendanceRate = (totalPresent + totalAbsent) > 0
                ? BigDecimal.valueOf(totalPresent * 100.0 / (totalPresent + totalAbsent))
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

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
    }
}