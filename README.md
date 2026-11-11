# School Timetable System

A modern Java desktop application for managing school timetables, built with modern Java (Java 21), object-oriented clean design principles, sealed classes, records, enums, interfaces, and a complete Java Swing Graphical User Interface (GUI).

---

## 🏛️ Architecture & UML Class Mapping

| Component | Java Feature | Role / Responsibility |
| :--- | :--- | :--- |
| `SubjectType` | `enum` | Enumerates school subjects with display names and string conversion utility stub. |
| `EntryStatus` | `enum` | Enumerates schedule statuses (`SCHEDULED`, `RESCHEDULED`, `CANCELLED`). |
| `RoomStatus` | `enum` | Enumerates classroom statuses (`AVAILABLE`, `OCCUPIED`, `MAINTENANCE`, `RESERVED`). |
| `PlatformType` | `enum` | Enumerates online meeting platforms (`ZOOM`, `TEAMS`, `GOOGLE_MEET`, `WEBEX`). |
| `LocationData` | `record` | Immutable record representing classroom location (`building`, `roomNumber`, `capacity`). |
| `StudentAttendance` | `record` | Immutable record for attendance tracking (`studentId`, `present`, `timestamp`). |
| `ScheduleEntry` | `interface` | Interface defining scheduling contracts, abstract getters, default `validate()` with private helper method, and static duration summation. |
| `TimetableItem` | `sealed abstract class` | Sealed base class implementing `ScheduleEntry`, strictly permitting `ClassroomLesson` and `OnlineLesson`. |
| `ClassroomLesson` | `final class` | Subclass for physical classroom sessions with teacher ID, room status, and location data. |
| `OnlineLesson` | `final class` | Subclass for virtual sessions with platform, meeting URL, and student attendance records. |
| `ItemCurrentlyActiveException` | `class (Exception)` | Custom checked exception thrown when an entry or room is currently active or in conflict. |
| `TimetableManager` | `class` | Central business manager maintaining timetable entries with defensive copying, stream queries, and reports. |
| `TimetableGUI` | `class (JFrame)` | Fully implemented Swing UI featuring `JList`, `DefaultListModel`, `JTextField`, `JComboBox`, dynamic forms, and functional event listeners. |
| `Main` | `class` | Application entry point that initializes native Look & Feel and launches the GUI on the Event Dispatch Thread (EDT). |

---

## 🚀 Getting Started

### Prerequisites
- **JDK 17 or JDK 21+** (e.g. OpenJDK 21 LTS or 25)
- **Apache Maven 3.8+**

### Building the Project
```bash
mvn clean package
```

### Running the Application
You can run the application directly with Maven:
```bash
mvn exec:java
```
Or execute the packaged JAR:
```bash
java -jar target/school-timetable-system-1.0.0-SNAPSHOT.jar
```

### Running Unit Tests
```bash
mvn test
```

---

## 📋 Implementation Guide: Completing the Domain Stubs

As requested, all core domain and manager methods are stubbed with `// TODO: Implement this method` and throw `UnsupportedOperationException("Not implemented yet: ...")`. Below is a guide on how to complete each method:

### 1. `SubjectType.java` & `EntryStatus.java`
- **Method:** `public static SubjectType fromString(String text)`
  - Use `Arrays.stream(values()).filter(...)` matching against `name()` or `getDisplayName()` (case-insensitive).

### 2. `ScheduleEntry.java`
- **Default Method:** `default void validate()`
  - Implement format validation and business rules (e.g., non-null values, positive durations).
- **Private Method:** `private void performFormatCheck()`
  - Validate that time ranges and identifiers conform to standard patterns.
- **Static Method:** `static Duration calculateTotalDuration(List<ScheduleEntry> entries)`
  - Sum durations using Java Streams:
    ```java
    return entries.stream()
                  .map(ScheduleEntry::getDuration)
                  .reduce(Duration.ZERO, Duration::plus);
    ```

### 3. `TimetableItem.java`
- **Method:** `public String getScheduledTime()`
  - Use `DateTimeFormatter` to format the start time and end time:
    ```java
    LocalTime endTime = startTime.plus(duration);
    return startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER);
    ```
- **Method:** `public boolean isAvailable()`
  - Check whether the entry status is not `CANCELLED` and time slots do not overlap.

### 4. `ClassroomLesson.java` & `OnlineLesson.java`
- Implement `getSpecifics()` returning formatted details for display:
  - In `ClassroomLesson`: Building, Room number, Capacity, Teacher ID, and Room Status.
  - In `OnlineLesson`: Platform name, Meeting URL, and count of attended students.

### 5. `TimetableManager.java`
- **Defensive Copying:**
  - `getLessons()`: Return `new ArrayList<>(this.lessons)`.
- **Add & Conflict Checking:**
  - In `addLesson(ScheduleEntry entry)`: Check for overlapping time on the same day for the same room or teacher; throw `ItemCurrentlyActiveException` if a conflict exists; otherwise add to `lessons`.
- **Weekly Report:**
  - Use Java Streams:
    ```java
    return lessons.stream()
                  .collect(Collectors.groupingBy(ScheduleEntry::getDay));
    ```
- **Teacher Schedule:**
  - Filter using pattern matching:
    ```java
    return lessons.stream()
                  .filter(e -> e instanceof ClassroomLesson cl && cl.getTeacherId().equalsIgnoreCase(teacherId))
                  .toList();
    ```
