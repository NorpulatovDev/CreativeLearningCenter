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

    private <T> List<T> safe(List<T> list) {
        return list == null ? List.of() : list;
    }

    private String teacherName(Group g) {
        return g != null && g.getTeacher() != null && g.getTeacher().getFullName() != null
                ? g.getTeacher().getFullName()
                : "Noma'lum";
    }

    /* ===================== DAILY ===================== */

    @Override
    public DailyReport getDailyReport(int year, int month, int day) {

        LocalDate date = LocalDate.of(year, month, day);

        List<Attendance> attendances = safe(attendanceRepository.findByDate(date));
        List<Payment> payments = safe(paymentRepository.findByPaidAtDate(date));

        int present = (int) attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        int absent = (int) attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        BigDecimal total = payments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, List<Attendance>> byGroup = attendances.stream()
                .filter(a -> a.getGroup() != null && a.getGroup().getId() != null)
                .collect(Collectors.groupingBy(a -> a.getGroup().getId()));

        List<GroupAttendanceSummary> groupStats = byGroup.values().stream()
                .map(list -> {
                    Group g = list.get(0).getGroup();
                    int p = (int) list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
                    int a = (int) list.stream().filter(at -> at.getStatus() == AttendanceStatus.ABSENT).count();
                    return GroupAttendanceSummary.builder()
                            .groupId(g.getId())
                            .groupName(Optional.ofNullable(g.getName()).orElse(""))
                            .teacherName(teacherName(g))
                            .presentCount(p)
                            .absentCount(a)
                            .totalStudents(p + a)
                            .build();
                }).toList();

        return DailyReport.builder()
                .date(date)
                .totalStudentsPresent(present)
                .totalStudentsAbsent(absent)
                .totalPaymentsReceived(total)
                .paymentCount(payments.size())
                .groupAttendances(groupStats)
                .payments(List.of())
                .build();
    }

    /* ===================== MONTHLY ===================== */

    @Override
    public MonthlyReport getMonthlyReport(int year, int month) {

        String key = year + "-" + String.format("%02d", month);
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        List<Group> groups = safe(groupRepository.findAll());
        List<Payment> payments = safe(paymentRepository.findByPaidForMonth(key));

        BigDecimal expected = BigDecimal.ZERO;
        BigDecimal actual = BigDecimal.ZERO;

        List<GroupMonthlyStats> stats = new ArrayList<>();
        Set<Long> paid = new HashSet<>();
        Set<Long> unpaid = new HashSet<>();

        for (Group g : groups) {
            List<StudentGroup> active = safe(
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
                    .collect(Collectors.toSet());

            paid.addAll(paidIds);

            active.forEach(sg -> unpaid.add(sg.getStudent().getId()));

            BigDecimal rate = groupExpected.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : groupActual.multiply(BigDecimal.valueOf(100))
                    .divide(groupExpected, 2, RoundingMode.HALF_UP);

            stats.add(GroupMonthlyStats.builder()
                    .groupId(g.getId())
                    .groupName(g.getName())
                    .teacherName(teacherName(g))
                    .activeStudents(active.size())
                    .expectedRevenue(groupExpected)
                    .actualRevenue(groupActual)
                    .paidStudents(paidIds.size())
                    .unpaidStudents(active.size() - paidIds.size())
                    .collectionRate(rate)
                    .build());
        }

        return MonthlyReport.builder()
                .year(year)
                .month(month)
                .monthName(monthName)
                .expectedRevenue(expected)
                .actualRevenue(actual)
                .collectionRate(
                        expected.compareTo(BigDecimal.ZERO) == 0
                                ? BigDecimal.ZERO
                                : actual.multiply(BigDecimal.valueOf(100))
                                .divide(expected, 2, RoundingMode.HALF_UP)
                )
                .studentsWhoPaid(paid.size())
                .studentsWhoDidNotPay(unpaid.size())
                .groupStats(stats)
                .build();
    }

    /* ===================== YEARLY ===================== */

    @Override
    public YearlyReport getYearlyReport(int year) {

        List<Payment> payments = safe(paymentRepository.findByYear(year));

        BigDecimal total = payments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return YearlyReport.builder()
                .year(year)
                .totalRevenue(total)
                .totalPayments(payments.size())
                .monthlyBreakdown(List.of())
                .teacherStats(List.of())
                .topGroups(List.of())
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
