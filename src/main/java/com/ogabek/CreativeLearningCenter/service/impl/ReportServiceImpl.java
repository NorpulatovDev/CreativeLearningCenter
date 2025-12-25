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

    /* ===================== HELPERS ===================== */

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private String getTeacherName(Group group) {
        if (group == null || group.getTeacher() == null) return "Noma'lum";
        return Optional.ofNullable(group.getTeacher().getFullName()).orElse("Noma'lum");
    }

    private void validateMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
    }

    private String safeMonthName(int month, TextStyle style) {
        try {
            return Month.of(month).getDisplayName(style, Locale.ENGLISH);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /* ===================== DAILY ===================== */

    @Override
    public DailyReport getDailyReport(int year, int month, int day) {
        validateMonth(month);
        LocalDate date = LocalDate.of(year, month, day);

        List<Attendance> attendances = safeList(attendanceRepository.findByDate(date));
        List<Payment> payments = safeList(paymentRepository.findByPaidAtDate(date));

        int present = (int) attendances.stream()
                .filter(a -> a != null && a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        int absent = (int) attendances.stream()
                .filter(a -> a != null && a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        BigDecimal totalPayments = payments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, List<Attendance>> byGroup = attendances.stream()
                .filter(a -> a != null && a.getGroup() != null && a.getGroup().getId() != null)
                .collect(Collectors.groupingBy(a -> a.getGroup().getId()));

        List<GroupAttendanceSummary> groupSummaries = byGroup.values().stream()
                .map(list -> {
                    Group g = list.get(0).getGroup();
                    int p = (int) list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
                    int a = (int) list.stream().filter(at -> at.getStatus() == AttendanceStatus.ABSENT).count();
                    return GroupAttendanceSummary.builder()
                            .groupId(g.getId())
                            .groupName(Optional.ofNullable(g.getName()).orElse(""))
                            .teacherName(getTeacherName(g))
                            .presentCount(p)
                            .absentCount(a)
                            .totalStudents(p + a)
                            .build();
                })
                .sorted(Comparator.comparing(GroupAttendanceSummary::getGroupName))
                .toList();

        List<PaymentSummary> paymentSummaries = payments.stream()
                .filter(p -> p != null && p.getStudent() != null && p.getGroup() != null)
                .map(p -> PaymentSummary.builder()
                        .paymentId(p.getId())
                        .studentName(Optional.ofNullable(p.getStudent().getFullName()).orElse(""))
                        .groupName(Optional.ofNullable(p.getGroup().getName()).orElse(""))
                        .amount(Optional.ofNullable(p.getAmount()).orElse(BigDecimal.ZERO))
                        .paidForMonth(Optional.ofNullable(p.getPaidForMonth()).orElse(""))
                        .build())
                .toList();

        return DailyReport.builder()
                .date(date)
                .totalStudentsPresent(present)
                .totalStudentsAbsent(absent)
                .totalPaymentsReceived(totalPayments)
                .paymentCount(payments.size())
                .groupAttendances(groupSummaries)
                .payments(paymentSummaries)
                .build();
    }

    /* ===================== MONTHLY ===================== */

    @Override
    public MonthlyReport getMonthlyReport(int year, int month) {
        validateMonth(month);

        String monthKey = year + "-" + String.format("%02d", month);
        String monthName = safeMonthName(month, TextStyle.FULL);

        List<Group> groups = safeList(groupRepository.findAll());
        List<Payment> payments = safeList(paymentRepository.findByPaidForMonth(monthKey));

        BigDecimal expected = BigDecimal.ZERO;
        BigDecimal actual = BigDecimal.ZERO;

        List<GroupMonthlyStats> stats = new ArrayList<>();
        Set<Long> paidStudents = new HashSet<>();
        Set<Long> unpaidStudents = new HashSet<>();

        for (Group g : groups) {
            if (g == null || g.getId() == null) continue;

            List<StudentGroup> active = safeList(
                    studentGroupRepository.findByGroupIdAndActiveTrue(g.getId())
            );

            if (active.isEmpty()) continue;

            BigDecimal fee = Optional.ofNullable(g.getMonthlyFee()).orElse(BigDecimal.ZERO);
            BigDecimal groupExpected = fee.multiply(BigDecimal.valueOf(active.size()));
            expected = expected.add(groupExpected);

            List<Payment> groupPayments = payments.stream()
                    .filter(p -> p.getGroup() != null && g.getId().equals(p.getGroup().getId()))
                    .toList();

            BigDecimal groupActual = groupPayments.stream()
                    .map(Payment::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            actual = actual.add(groupActual);

            Set<Long> paidIds = groupPayments.stream()
                    .map(Payment::getStudent)
                    .filter(Objects::nonNull)
                    .map(Student::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            paidStudents.addAll(paidIds);

            active.forEach(sg -> {
                if (sg.getStudent() != null && !paidIds.contains(sg.getStudent().getId())) {
                    unpaidStudents.add(sg.getStudent().getId());
                }
            });

            BigDecimal rate = groupExpected.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : groupActual.multiply(BigDecimal.valueOf(100))
                    .divide(groupExpected, 2, RoundingMode.HALF_UP);

            stats.add(GroupMonthlyStats.builder()
                    .groupId(g.getId())
                    .groupName(Optional.ofNullable(g.getName()).orElse(""))
                    .teacherName(getTeacherName(g))
                    .activeStudents(active.size())
                    .expectedRevenue(groupExpected)
                    .actualRevenue(groupActual)
                    .paidStudents(paidIds.size())
                    .unpaidStudents(active.size() - paidIds.size())
                    .collectionRate(rate)
                    .build());
        }

        BigDecimal collectionRate = expected.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : actual.multiply(BigDecimal.valueOf(100))
                .divide(expected, 2, RoundingMode.HALF_UP);

        return MonthlyReport.builder()
                .year(year)
                .month(month)
                .monthName(monthName)
                .totalGroups(groups.size())
                .expectedRevenue(expected)
                .actualRevenue(actual)
                .collectionRate(collectionRate)
                .totalPayments(payments.size())
                .studentsWhoPaid(paidStudents.size())
                .studentsWhoDidNotPay(unpaidStudents.size())
                .groupStats(stats)
                .build();
    }

    /* ===================== YEARLY ===================== */

    @Override
    public YearlyReport getYearlyReport(int year) {

        List<Payment> payments = safeList(paymentRepository.findByYear(year));

        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MonthlyRevenueSummary> monthly = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            String key = year + "-" + String.format("%02d", m);

            List<Payment> monthPayments = payments.stream()
                    .filter(p -> key.equals(p.getPaidForMonth()))
                    .toList();

            BigDecimal revenue = monthPayments.stream()
                    .map(Payment::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthly.add(MonthlyRevenueSummary.builder()
                    .month(m)
                    .monthName(safeMonthName(m, TextStyle.SHORT))
                    .revenue(revenue)
                    .paymentCount(monthPayments.size())
                    .build());
        }

        return YearlyReport.builder()
                .year(year)
                .totalRevenue(totalRevenue)
                .totalPayments(payments.size())
                .monthlyBreakdown(monthly)
                .build();
    }
}
