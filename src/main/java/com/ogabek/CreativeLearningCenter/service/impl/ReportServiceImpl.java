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
        if (group == null || group.getTeacher() == null) {
            return "Noma'lum";
        }
        return group.getTeacher().getFullName();
    }

    @Override
    public DailyReport getDailyReport(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        log.info("Generating daily report for {}", date);

        List<Attendance> attendances = attendanceRepository.findByDate(date);
        if (attendances == null) attendances = new ArrayList<>();

        List<Payment> payments = paymentRepository.findByPaidAtDate(date);
        if (payments == null) payments = new ArrayList<>();

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
                .filter(a -> a.getGroup() != null)
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

                    return GroupAttendanceSummary.builder()
                            .groupId(first.getGroup().getId())
                            .groupName(first.getGroup().getName())
                            .teacherName(getTeacherName(first.getGroup()))
                            .presentCount(present)
                            .absentCount(absent)
                            .totalStudents(present + absent)
                            .build();
                })
                .sorted(Comparator.comparing(GroupAttendanceSummary::getGroupName))
                .toList();

        List<PaymentSummary> paymentSummaries = payments.stream()
                .filter(p -> p.getStudent() != null && p.getGroup() != null)
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

        List<Group> allGroups = groupRepository.findAll();
        if (allGroups == null) allGroups = new ArrayList<>();

        List<Payment> monthPayments = paymentRepository.findByPaidForMonth(monthKey);
        if (monthPayments == null) monthPayments = new ArrayList<>();

        // Calculate expected and actual revenue per group
        List<GroupMonthlyStats> groupStats = new ArrayList<>();
        Set<Long> studentsWhoPaid = new HashSet<>();
        Set<Long> studentsWhoDidNotPay = new HashSet<>();

        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;

        for (Group group : allGroups) {
            // Skip groups without teacher
            if (group.getTeacher() == null) continue;

            List<StudentGroup> activeEnrollments = studentGroupRepository.findByGroupIdAndActiveTrue(group.getId());
            if (activeEnrollments == null) activeEnrollments = new ArrayList<>();

            int activeStudents = activeEnrollments.size();

            if (activeStudents == 0) continue;

            BigDecimal groupFee = group.getMonthlyFee() != null ? group.getMonthlyFee() : BigDecimal.ZERO;
            BigDecimal expectedForGroup = groupFee.multiply(BigDecimal.valueOf(activeStudents));
            totalExpected = totalExpected.add(expectedForGroup);

            // Find payments for this group in this month
            final List<Payment> finalMonthPayments = monthPayments;
            List<Payment> groupPayments = finalMonthPayments.stream()
                    .filter(p -> p.getGroup() != null && p.getGroup().getId().equals(group.getId()))
                    .toList();

            BigDecimal actualForGroup = groupPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalActual = totalActual.add(actualForGroup);

            Set<Long> paidStudentIds = groupPayments.stream()
                    .filter(p -> p.getStudent() != null)
                    .map(p -> p.getStudent().getId())
                    .collect(Collectors.toSet());

            int paidCount = paidStudentIds.size();
            int unpaidCount = activeStudents - paidCount;

            studentsWhoPaid.addAll(paidStudentIds);

            // Track unpaid students
            for (StudentGroup enrollment : activeEnrollments) {
                if (enrollment.getStudent() != null && !paidStudentIds.contains(enrollment.getStudent().getId())) {
                    studentsWhoDidNotPay.add(enrollment.getStudent().getId());
                }
            }

            BigDecimal collectionRate = expectedForGroup.compareTo(BigDecimal.ZERO) > 0
                    ? actualForGroup.multiply(BigDecimal.valueOf(100))
                    .divide(expectedForGroup, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            groupStats.add(GroupMonthlyStats.builder()
                    .groupId(group.getId())
                    .groupName(group.getName())
                    .teacherName(getTeacherName(group))
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
        final List<Payment> finalMonthPayments2 = monthPayments;
        for (Group group : allGroups) {
            if (group.getTeacher() == null) continue;

            List<StudentGroup> activeEnrollments = studentGroupRepository.findByGroupIdAndActiveTrue(group.getId());
            if (activeEnrollments == null) activeEnrollments = new ArrayList<>();

            Set<Long> paidForGroup = finalMonthPayments2.stream()
                    .filter(p -> p.getGroup() != null && p.getGroup().getId().equals(group.getId()) && p.getStudent() != null)
                    .map(p -> p.getStudent().getId())
                    .collect(Collectors.toSet());

            for (StudentGroup enrollment : activeEnrollments) {
                Student student = enrollment.getStudent();
                if (student != null && !paidForGroup.contains(student.getId())) {
                    BigDecimal fee = group.getMonthlyFee() != null ? group.getMonthlyFee() : BigDecimal.ZERO;
                    unpaidStudentsList.add(StudentPaymentStatus.builder()
                            .studentId(student.getId())
                            .studentName(student.getFullName())
                            .parentName(student.getParentName())
                            .parentPhoneNumber(student.getParentPhoneNumber())
                            .groupId(group.getId())
                            .groupName(group.getName())
                            .amountDue(fee)
                            .hasPaid(false)
                            .build());
                }
            }
        }

        // Attendance stats for the month
        List<Attendance> monthAttendances = attendanceRepository.findByMonth(year, month);
        if (monthAttendances == null) monthAttendances = new ArrayList<>();

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

        List<StudentGroup> allEnrollments = studentGroupRepository.findAll();
        if (allEnrollments == null) allEnrollments = new ArrayList<>();

        int totalActiveStudents = allEnrollments.stream()
                .filter(sg -> sg.getActive() != null && sg.getActive() && sg.getStudent() != null)
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
        if (yearPayments == null) yearPayments = new ArrayList<>();

        BigDecimal totalRevenue = yearPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Monthly breakdown
        List<MonthlyRevenueSummary> monthlyBreakdown = new ArrayList<>();
        final List<Payment> finalYearPayments = yearPayments;

        for (int month = 1; month <= 12; month++) {
            String monthKey = year + "-" + String.format("%02d", month);
            List<Payment> monthPayments = finalYearPayments.stream()
                    .filter(p -> monthKey.equals(p.getPaidForMonth()))
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
        if (teachers == null) teachers = new ArrayList<>();

        List<TeacherYearlyStats> teacherStats = new ArrayList<>();

        for (Teacher teacher : teachers) {
            if (teacher == null) continue;

            List<Group> teacherGroups = groupRepository.findByTeacherId(teacher.getId());
            if (teacherGroups == null) teacherGroups = new ArrayList<>();

            int totalStudents = 0;
            for (Group g : teacherGroups) {
                Integer count = studentGroupRepository.countActiveByGroupId(g.getId());
                totalStudents += (count != null ? count : 0);
            }

            final List<Group> finalTeacherGroups = teacherGroups;
            BigDecimal teacherRevenue = finalYearPayments.stream()
                    .filter(p -> p.getGroup() != null && finalTeacherGroups.stream()
                            .anyMatch(g -> g.getId().equals(p.getGroup().getId())))
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            teacherStats.add(TeacherYearlyStats.builder()
                    .teacherId(teacher.getId())
                    .teacherName(teacher.getFullName())
                    .groupCount(teacherGroups.size())
                    .totalStudents(totalStudents)
                    .totalRevenue(teacherRevenue)
                    .build());
        }

        // Sort by revenue descending
        teacherStats.sort((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()));

        // Top groups by revenue
        List<Group> allGroups = groupRepository.findAll();
        if (allGroups == null) allGroups = new ArrayList<>();

        List<GroupYearlyStats> topGroups = new ArrayList<>();

        for (Group group : allGroups) {
            if (group == null) continue;

            List<Payment> groupPayments = finalYearPayments.stream()
                    .filter(p -> p.getGroup() != null && p.getGroup().getId().equals(group.getId()))
                    .toList();

            BigDecimal groupRevenue = groupPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            topGroups.add(GroupYearlyStats.builder()
                    .groupId(group.getId())
                    .groupName(group.getName())
                    .teacherName(getTeacherName(group))
                    .totalRevenue(groupRevenue)
                    .totalPayments(groupPayments.size())
                    .build());
        }

        // Sort by revenue descending and limit to 10
        topGroups.sort((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()));
        if (topGroups.size() > 10) {
            topGroups = topGroups.subList(0, 10);
        }

        // Yearly attendance stats
        int totalPresent = 0;
        int totalAbsent = 0;
        for (int month = 1; month <= 12; month++) {
            List<Attendance> monthAttendances = attendanceRepository.findByMonth(year, month);
            if (monthAttendances == null) continue;

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