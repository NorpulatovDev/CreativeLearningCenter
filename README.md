# Creative Learning Center Management System

A comprehensive Spring Boot backend for managing learning centers with support for teachers, groups, students, attendance tracking, payments, and detailed reports.

## Features

- **Teacher Management**: Create, update, delete teachers with income tracking
- **Group Management**: Manage learning groups with monthly fees
- **Student Management**: Multi-group enrollment support with payment status tracking
- **Attendance Tracking**: Daily attendance with present/absent status
- **Payment Processing**: Track payments per student per group per month
- **Comprehensive Reports**: Daily, Monthly, and Yearly reports

## API Endpoints

### Authentication
- `POST /auth/login` - Login and get JWT token

### Teachers
- `GET /api/teachers` - Get all teachers
- `GET /api/teachers/{id}` - Get teacher by ID
- `POST /api/teachers` - Create teacher
- `PUT /api/teachers/{id}` - Update teacher
- `DELETE /api/teachers/{id}` - Delete teacher

### Groups
- `GET /api/groups` - Get all groups
- `GET /api/groups/{id}` - Get group by ID
- `GET /api/groups/teacher/{teacherId}` - Get groups by teacher
- `POST /api/groups` - Create group
- `PUT /api/groups/{id}` - Update group
- `DELETE /api/groups/{id}` - Delete group

### Students
- `GET /api/students` - Get all students (includes payment status)
- `GET /api/students/{id}` - Get student by ID
- `GET /api/students/group/{groupId}` - Get students by group
- `POST /api/students` - Create student
- `PUT /api/students/{id}` - Update student

### Enrollments
- `POST /api/enrollments` - Enroll student in group
- `DELETE /api/enrollments/student/{studentId}/group/{groupId}` - Remove from group
- `GET /api/enrollments/student/{studentId}` - Get student's groups
- `GET /api/enrollments/group/{groupId}` - Get group's students

### Attendance
- `POST /api/attendances` - Create attendance for group
- `GET /api/attendances/{id}` - Get attendance by ID
- `GET /api/attendances/group/{groupId}/date/{date}` - Get by group and date
- `GET /api/attendances/month/{year}/{month}` - Get by month
- `PATCH /api/attendances/{id}` - Update attendance status

### Payments
- `POST /api/payments` - Create payment
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/student/{studentId}` - Get by student
- `GET /api/payments/group/{groupId}` - Get by group

### Reports
- `GET /api/reports/daily/{year}/{month}/{day}` - Get daily report
- `GET /api/reports/monthly/{year}/{month}` - Get monthly report
- `GET /api/reports/yearly/{year}` - Get yearly report

## Report Details

### Daily Report
- Total students present/absent
- Total payments received
- Per-group attendance summary
- Payment details

### Monthly Report
- Expected vs actual revenue
- Collection rate percentage
- Per-group statistics
- **List of unpaid students with contact info**
- Attendance statistics

### Yearly Report
- Total revenue and payment count
- Monthly revenue breakdown
- Teacher performance statistics
- Top performing groups
- Yearly attendance rate

## Student Payment Status

When fetching students, the response includes:
- `paidForCurrentMonth`: Boolean indicating if all groups are paid
- `groupsPaidCount`: Number of groups paid for current month
- `groupsUnpaidCount`: Number of groups unpaid
- Per-group payment status with `paidForCurrentMonth` flag

## Tech Stack

- Java 21
- Spring Boot 3.4.1
- Spring Security with JWT
- Spring Data JPA
- H2 Database (dev) / PostgreSQL (prod)
- Swagger/OpenAPI for documentation

## Running the Application

```bash
./gradlew bootRun
```

Access:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## Default Credentials

- Username: `admin`
- Password: `admin123`

## Configuration

See `application.properties` for configuration options including:
- Database settings
- JWT secret and expiration
- Admin credentials
# CreativeLearningCenter
