package com.ogabek.CreativeLearningCenter.config;

import com.ogabek.CreativeLearningCenter.entity.*;
import com.ogabek.CreativeLearningCenter.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DemoDataConfig {

    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

    @Bean
    @Transactional
    public CommandLineRunner loadDemoData() {
        return args -> {
            log.info("===== Starting Demo Data Initialization =====");

            // Create Teachers
            List<Teacher> teachers = createTeachers();
            log.info("Created {} teachers", teachers.size());

            // Create Groups
            List<Group> groups = createGroups(teachers);
            log.info("Created {} groups", groups.size());

            // Create Students
            List<Student> students = createStudents();
            log.info("Created {} students", students.size());

            // Enroll Students in Groups
            List<StudentGroup> enrollments = enrollStudentsInGroups(students, groups);
            log.info("Created {} student enrollments", enrollments.size());

            // Create Payments
            List<Payment> payments = createPayments(enrollments);
            log.info("Created {} payments", payments.size());

            // Create Attendance Records
            List<Attendance> attendances = createAttendanceRecords(enrollments);
            log.info("Created {} attendance records", attendances.size());

            log.info("===== Demo Data Initialization Completed =====");
        };
    }

    private List<Teacher> createTeachers() {
        List<Teacher> teachers = new ArrayList<>();

        teachers.add(Teacher.builder()
                .fullName("Aziza Karimova")
                .phoneNumber("+998901234567")
                .build());

        teachers.add(Teacher.builder()
                .fullName("Jasur Alimov")
                .phoneNumber("+998901234568")
                .build());

        teachers.add(Teacher.builder()
                .fullName("Dilnoza Rahimova")
                .phoneNumber("+998901234569")
                .build());

        teachers.add(Teacher.builder()
                .fullName("Sherzod Tursunov")
                .phoneNumber("+998901234570")
                .build());

        teachers.add(Teacher.builder()
                .fullName("Nodira Mustafaeva")
                .phoneNumber("+998901234571")
                .build());

        return teacherRepository.saveAll(teachers);
    }

    private List<Group> createGroups(List<Teacher> teachers) {
        List<Group> groups = new ArrayList<>();

        // Groups for Teacher 1 - Aziza Karimova
        groups.add(Group.builder()
                .name("English Beginners A1")
                .teacher(teachers.get(0))
                .monthlyFee(new BigDecimal("300000"))
                .build());

        groups.add(Group.builder()
                .name("English Elementary A2")
                .teacher(teachers.get(0))
                .monthlyFee(new BigDecimal("350000"))
                .build());

        // Groups for Teacher 2 - Jasur Alimov
        groups.add(Group.builder()
                .name("Mathematics Grade 5")
                .teacher(teachers.get(1))
                .monthlyFee(new BigDecimal("250000"))
                .build());

        groups.add(Group.builder()
                .name("Mathematics Grade 7")
                .teacher(teachers.get(1))
                .monthlyFee(new BigDecimal("280000"))
                .build());

        // Groups for Teacher 3 - Dilnoza Rahimova
        groups.add(Group.builder()
                .name("Piano Basics")
                .teacher(teachers.get(2))
                .monthlyFee(new BigDecimal("400000"))
                .build());

        groups.add(Group.builder()
                .name("Piano Advanced")
                .teacher(teachers.get(2))
                .monthlyFee(new BigDecimal("500000"))
                .build());

        // Groups for Teacher 4 - Sherzod Tursunov
        groups.add(Group.builder()
                .name("Programming Python")
                .teacher(teachers.get(3))
                .monthlyFee(new BigDecimal("450000"))
                .build());

        groups.add(Group.builder()
                .name("Programming Java")
                .teacher(teachers.get(3))
                .monthlyFee(new BigDecimal("450000"))
                .build());

        // Groups for Teacher 5 - Nodira Mustafaeva
        groups.add(Group.builder()
                .name("Chess Beginners")
                .teacher(teachers.get(4))
                .monthlyFee(new BigDecimal("200000"))
                .build());

        groups.add(Group.builder()
                .name("Chess Advanced")
                .teacher(teachers.get(4))
                .monthlyFee(new BigDecimal("250000"))
                .build());

        return groupRepository.saveAll(groups);
    }

    private List<Student> createStudents() {
        List<Student> students = new ArrayList<>();

        students.add(Student.builder()
                .fullName("Ali Nazarov")
                .parentName("Shohruh Nazarov")
                .parentPhoneNumber("+998901111111")
                .smsLinkCode("STU-10001")
                .build());

        students.add(Student.builder()
                .fullName("Madina Yusupova")
                .parentName("Gulnora Yusupova")
                .parentPhoneNumber("+998901111112")
                .smsLinkCode("STU-10002")
                .build());

        students.add(Student.builder()
                .fullName("Bekzod Sharipov")
                .parentName("Otabek Sharipov")
                .parentPhoneNumber("+998901111113")
                .smsLinkCode("STU-10003")
                .build());

        students.add(Student.builder()
                .fullName("Zarina Abdullayeva")
                .parentName("Feruza Abdullayeva")
                .parentPhoneNumber("+998901111114")
                .smsLinkCode("STU-10004")
                .build());

        students.add(Student.builder()
                .fullName("Davron Karimov")
                .parentName("Jamshid Karimov")
                .parentPhoneNumber("+998901111115")
                .smsLinkCode("STU-10005")
                .build());

        students.add(Student.builder()
                .fullName("Sevara Rustamova")
                .parentName("Nargiza Rustamova")
                .parentPhoneNumber("+998901111116")
                .smsLinkCode("STU-10006")
                .build());

        students.add(Student.builder()
                .fullName("Sardor Ibragimov")
                .parentName("Azim Ibragimov")
                .parentPhoneNumber("+998901111117")
                .smsLinkCode("STU-10007")
                .build());

        students.add(Student.builder()
                .fullName("Kamila Nurmatova")
                .parentName("Dilfuza Nurmatova")
                .parentPhoneNumber("+998901111118")
                .smsLinkCode("STU-10008")
                .build());

        students.add(Student.builder()
                .fullName("Anvar Salimov")
                .parentName("Rustam Salimov")
                .parentPhoneNumber("+998901111119")
                .smsLinkCode("STU-10009")
                .build());

        students.add(Student.builder()
                .fullName("Dilshoda Mirzoeva")
                .parentName("Malika Mirzoeva")
                .parentPhoneNumber("+998901111120")
                .smsLinkCode("STU-10010")
                .build());

        students.add(Student.builder()
                .fullName("Jahongir Hasanov")
                .parentName("Ulugbek Hasanov")
                .parentPhoneNumber("+998901111121")
                .smsLinkCode("STU-10011")
                .build());

        students.add(Student.builder()
                .fullName("Nilufar Tashmatova")
                .parentName("Zamira Tashmatova")
                .parentPhoneNumber("+998901111122")
                .smsLinkCode("STU-10012")
                .build());

        students.add(Student.builder()
                .fullName("Timur Ergashev")
                .parentName("Bobur Ergashev")
                .parentPhoneNumber("+998901111123")
                .smsLinkCode("STU-10013")
                .build());

        students.add(Student.builder()
                .fullName("Gulnoza Azimova")
                .parentName("Munira Azimova")
                .parentPhoneNumber("+998901111124")
                .smsLinkCode("STU-10014")
                .build());

        students.add(Student.builder()
                .fullName("Ravshan Mamatov")
                .parentName("Alisher Mamatov")
                .parentPhoneNumber("+998901111125")
                .smsLinkCode("STU-10015")
                .build());

        return studentRepository.saveAll(students);
    }

    private List<StudentGroup> enrollStudentsInGroups(List<Student> students, List<Group> groups) {
        List<StudentGroup> enrollments = new ArrayList<>();
        Random random = new Random();

        // Track unique enrollments to prevent duplicates
        Set<String> enrollmentKeys = new HashSet<>();

        // Enroll each student in 1-3 random groups
        for (Student student : students) {
            int numGroups = 1 + random.nextInt(3); // 1 to 3 groups
            Set<Group> selectedGroups = new HashSet<>();

            while (selectedGroups.size() < numGroups && selectedGroups.size() < groups.size()) {
                Group randomGroup = groups.get(random.nextInt(groups.size()));
                String enrollmentKey = student.getId() + "-" + randomGroup.getId();

                if (!enrollmentKeys.contains(enrollmentKey)) {
                    selectedGroups.add(randomGroup);
                    enrollmentKeys.add(enrollmentKey);

                    enrollments.add(StudentGroup.builder()
                            .student(student)
                            .group(randomGroup)
                            .active(true)
                            .enrolledAt(LocalDate.now().minusDays(random.nextInt(90)))
                            .build());
                }
            }
        }

        // Add some inactive enrollments (students who left)
        int inactiveCount = 0;
        int maxAttempts = 20;
        int attempts = 0;

        while (inactiveCount < 5 && attempts < maxAttempts) {
            attempts++;
            Student student = students.get(random.nextInt(students.size()));
            Group group = groups.get(random.nextInt(groups.size()));
            String enrollmentKey = student.getId() + "-" + group.getId();

            if (!enrollmentKeys.contains(enrollmentKey)) {
                enrollmentKeys.add(enrollmentKey);
                enrollments.add(StudentGroup.builder()
                        .student(student)
                        .group(group)
                        .active(false)
                        .enrolledAt(LocalDate.now().minusDays(120 + random.nextInt(60)))
                        .leftAt(LocalDate.now().minusDays(random.nextInt(30)))
                        .build());
                inactiveCount++;
            }
        }

        return studentGroupRepository.saveAll(enrollments);
    }

    private List<Payment> createPayments(List<StudentGroup> enrollments) {
        List<Payment> payments = new ArrayList<>();
        Random random = new Random();

        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue());

        for (StudentGroup enrollment : enrollments) {
            if (!enrollment.getActive()) {
                continue; // Skip inactive enrollments
            }

            // Create payment for current month (70% probability)
            if (random.nextDouble() < 0.7) {
                payments.add(Payment.builder()
                        .student(enrollment.getStudent())
                        .group(enrollment.getGroup())
                        .amount(enrollment.getGroup().getMonthlyFee())
                        .paidForMonth(currentMonth)
                        .build());
            }

            // Create payment for previous month (50% probability)
            LocalDate previousMonth = currentDate.minusMonths(1);
            String prevMonth = previousMonth.getYear() + "-" + String.format("%02d", previousMonth.getMonthValue());
            if (random.nextDouble() < 0.5) {
                payments.add(Payment.builder()
                        .student(enrollment.getStudent())
                        .group(enrollment.getGroup())
                        .amount(enrollment.getGroup().getMonthlyFee())
                        .paidForMonth(prevMonth)
                        .build());
            }

            // Create payment for 2 months ago (30% probability)
            LocalDate twoMonthsAgo = currentDate.minusMonths(2);
            String twoMonthsAgoStr = twoMonthsAgo.getYear() + "-" + String.format("%02d", twoMonthsAgo.getMonthValue());
            if (random.nextDouble() < 0.3) {
                payments.add(Payment.builder()
                        .student(enrollment.getStudent())
                        .group(enrollment.getGroup())
                        .amount(enrollment.getGroup().getMonthlyFee())
                        .paidForMonth(twoMonthsAgoStr)
                        .build());
            }
        }

        return paymentRepository.saveAll(payments);
    }

    private List<Attendance> createAttendanceRecords(List<StudentGroup> enrollments) {
        List<Attendance> attendances = new ArrayList<>();
        Random random = new Random();

        // Track unique attendance records
        Set<String> attendanceKeys = new HashSet<>();

        // Create attendance for the last 30 days
        LocalDate currentDate = LocalDate.now();

        for (int dayOffset = 0; dayOffset < 30; dayOffset++) {
            LocalDate date = currentDate.minusDays(dayOffset);

            // Skip weekends
            if (date.getDayOfWeek().getValue() >= 6) {
                continue;
            }

            for (StudentGroup enrollment : enrollments) {
                if (!enrollment.getActive()) {
                    continue;
                }

                // Only create attendance if enrollment date is before this date
                if (enrollment.getEnrolledAt().isAfter(date)) {
                    continue;
                }

                String attendanceKey = enrollment.getStudent().getId() + "-" +
                        enrollment.getGroup().getId() + "-" + date.toString();

                if (!attendanceKeys.contains(attendanceKey)) {
                    attendanceKeys.add(attendanceKey);

                    // 90% probability of being present
                    AttendanceStatus status = random.nextDouble() < 0.9
                            ? AttendanceStatus.PRESENT
                            : AttendanceStatus.ABSENT;

                    attendances.add(Attendance.builder()
                            .student(enrollment.getStudent())
                            .group(enrollment.getGroup())
                            .date(date)
                            .status(status)
                            .build());
                }
            }
        }

        return attendanceRepository.saveAll(attendances);
    }
}
