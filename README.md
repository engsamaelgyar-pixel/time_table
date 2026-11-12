# School Timetable System (Lightweight Local Setup)

A clean, lightweight desktop Java Swing application for managing school timetables using **Modern Java (Java 21 LTS)**, object-oriented design principles (sealed classes, records, enums, interfaces), and a local **MySQL** database connected via plain JDBC.

Zero Docker, zero cloud dependencies, and zero complex configuration files — runs directly out of your IDE (like IntelliJ IDEA) with a single click.

---

## 📁 Project Structure

```text
c:\java_asmaa\time_table\
├── pom.xml                               # Standard Maven build file
├── README.md                             # Step-by-step setup guide
├── run.bat                               # Optional 1-click batch launcher
├── sql\
│   └── schema.sql                        # Single SQL script (Database + Tables + Sample Data)
└── src\
    ├── main\java\com\school\timetable\
    │   ├── Main.java                     # Application Launcher (Click 'Run' here)
    │   ├── database\
    │   │   ├── DatabaseConnection.java   # Plain JDBC connection (URL, USER, PASSWORD)
    │   │   ├── TimetableDAO.java         # Data Access Object interface
    │   │   └── TimetableDAOImpl.java     # JDBC CRUD operations with MySQL
    │   ├── exception\
    │   │   └── ItemCurrentlyActiveException.java
    │   ├── gui\
    │   │   └── TimetableGUI.java         # Fully implemented Java Swing UI
    │   ├── manager\
    │   │   └── TimetableManager.java     # Business manager with method stubs
    │   └── model\
    │       ├── ClassroomLesson.java      # Final class (in-person lesson)
    │       ├── OnlineLesson.java         # Final class (remote lesson)
    │       ├── TimetableItem.java        # Sealed abstract base class
    │       ├── ScheduleEntry.java        # Core interface (default/private/static methods)
    │       ├── LocationData.java         # Record (building, room, capacity)
    │       ├── StudentAttendance.java    # Record (studentId, present, timestamp)
    │       ├── EntryStatus.java          # Enum (SCHEDULED, RESCHEDULED, CANCELLED)
    │       ├── PlatformType.java         # Enum (ZOOM, TEAMS, GOOGLE_MEET, WEBEX)
    │       ├── RoomStatus.java           # Enum (AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED)
    │       └── SubjectType.java          # Enum (MATH, SCIENCE, ENGLISH, etc.)
    └── test\java\com\school\timetable\
        └── TimetableSystemTest.java      # Automated unit tests
```

---

## ⚡ 3-Step Setup Guide

### Step 1: Set Up Local MySQL Database
Open your preferred local MySQL tool:
- **MySQL Workbench**: Click **File** → **Open SQL Script**, select [`sql/schema.sql`](file:///c:/java_asmaa/time_table/sql/schema.sql), and click the **Lightning Bolt (⚡)** button.
- **phpMyAdmin (XAMPP)**: Go to `http://localhost/phpmyadmin`, click the **SQL** tab, paste the contents of `sql/schema.sql`, and click **Go**.

*This creates the database `school_timetable_db`, creates all 5 tables, and inserts sample rows.*

### Step 2: Configure Database Credentials (if needed)
In [`DatabaseConnection.java`](file:///c:/java_asmaa/time_table/src/main/java/com/school/timetable/database/DatabaseConnection.java), check the credentials:
```java
private static final String URL = "jdbc:mysql://localhost:3306/school_timetable_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = ""; // Leave empty for default XAMPP, or enter your MySQL root password
```

### Step 3: Run the Application
- Open the project in **IntelliJ IDEA**.
- Navigate to `src/main/java/com/school/timetable/Main.java`.
- Click the green **Run ▶** button next to `public static void main(String[] args)`.

*(Or simply double-click [`run.bat`](file:///c:/java_asmaa/time_table/run.bat) or type `mvn compile exec:java` in your terminal).*

---

## 🧪 Running Unit Tests
In IntelliJ IDEA or terminal:
```bash
mvn test
```
All unit tests verify sealed class constraints, record immutability, and method stubs.
