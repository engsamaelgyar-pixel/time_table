package com.school.timetable;

import com.school.timetable.exception.ItemCurrentlyActiveException;
import com.school.timetable.manager.TimetableManager;
import com.school.timetable.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying domain model integrity, sealed hierarchy,
 * records, and expected stub behavior.
 */
public class TimetableSystemTest {

    @Test
    @DisplayName("Verify LocationData and StudentAttendance record immutability and fields")
    void testRecords() {
        LocationData loc = new LocationData("Building A", "Room 101", "30");
        assertEquals("Building A", loc.building());
        assertEquals("Room 101", loc.roomNumber());
        assertEquals("30", loc.capacity());

        LocationData loc2 = new LocationData("Room 102", "25");
        assertEquals("Main Campus", loc2.building());
        assertEquals("Room 102", loc2.roomNumber());

        StudentAttendance attendance = new StudentAttendance("STU-1", true, LocalDateTime.now());
        assertEquals("STU-1", attendance.studentId());
        assertTrue(attendance.present());
        assertNotNull(attendance.timestamp());
    }

    @Test
    @DisplayName("Verify Sealed hierarchy permits ClassroomLesson and OnlineLesson")
    void testSealedHierarchy() {
        Class<?> superclass = TimetableItem.class;
        assertTrue(superclass.isSealed(), "TimetableItem must be a sealed class");

        Class<?>[] permitted = superclass.getPermittedSubclasses();
        assertNotNull(permitted);
        assertEquals(2, permitted.length);
        assertTrue(List.of(permitted).contains(ClassroomLesson.class));
        assertTrue(List.of(permitted).contains(OnlineLesson.class));
    }

    @Test
    @DisplayName("Verify ClassroomLesson instantiation and field getters")
    void testClassroomLesson() {
        LocationData loc = new LocationData("Science Wing", "Lab 201", "35");
        ClassroomLesson lesson = new ClassroomLesson(
                "CLS-101", Duration.ofMinutes(60), DayOfWeek.MONDAY, LocalTime.of(9, 0),
                SubjectType.MATH, EntryStatus.SCHEDULED,
                loc, "TCH-SMITH", RoomStatus.AVAILABLE);

        assertEquals("CLS-101", lesson.getItemId());
        assertEquals(Duration.ofMinutes(60), lesson.getDuration());
        assertEquals(DayOfWeek.MONDAY, lesson.getDay());
        assertEquals(LocalTime.of(9, 0), lesson.getStartTime());
        assertEquals(SubjectType.MATH, lesson.getSubject());
        assertEquals(EntryStatus.SCHEDULED, lesson.getStatus());
        assertEquals("TCH-SMITH", lesson.getTeacherId());
        assertEquals(RoomStatus.AVAILABLE, lesson.getRoomStatus());
        assertEquals(loc, lesson.getLocation());
    }

    @Test
    @DisplayName("Verify OnlineLesson instantiation and defensive copy of attendance")
    void testOnlineLesson() {
        StudentAttendance att = new StudentAttendance("STU-001", true, LocalDateTime.now());
        OnlineLesson lesson = new OnlineLesson(
                "ONL-201", Duration.ofMinutes(45), DayOfWeek.WEDNESDAY, LocalTime.of(14, 0),
                SubjectType.SCIENCE, EntryStatus.SCHEDULED,
                PlatformType.TEAMS, "https://teams.microsoft.com/meet/123",
                List.of(att));

        assertEquals("ONL-201", lesson.getItemId());
        assertEquals(PlatformType.TEAMS, lesson.getPlatform());
        assertEquals("Microsoft Teams", lesson.getMeetingPlatform());
        assertEquals(1, lesson.getAttendanceRecords().size());

        // Verify defensive copy
        List<StudentAttendance> records = lesson.getAttendanceRecords();
        records.clear();
        assertEquals(1, lesson.getAttendanceRecords().size(), "getAttendanceRecords must return defensive copy");
    }

    @Test
    @DisplayName("Verify that domain and manager stub methods throw UnsupportedOperationException as requested")
    void testDomainStubsThrowException() {
        LocationData loc = new LocationData("Science Wing", "Lab 201", "35");
        ClassroomLesson lesson = new ClassroomLesson(
                "CLS-101", Duration.ofMinutes(60), DayOfWeek.MONDAY, LocalTime.of(9, 0),
                SubjectType.MATH, EntryStatus.SCHEDULED,
                loc, "TCH-SMITH", RoomStatus.AVAILABLE);

        assertThrows(UnsupportedOperationException.class, lesson::getScheduledTime);
        assertThrows(UnsupportedOperationException.class, lesson::isAvailable);
        assertThrows(UnsupportedOperationException.class, lesson::getSpecifics);
        assertThrows(UnsupportedOperationException.class, lesson::validate);

        TimetableManager manager = new TimetableManager();
        assertThrows(UnsupportedOperationException.class, manager::getLessons);
        assertThrows(UnsupportedOperationException.class, () -> manager.addLesson(lesson));
        assertThrows(UnsupportedOperationException.class, () -> manager.removeLesson(lesson));
        assertThrows(UnsupportedOperationException.class, manager::generateReport);
        assertThrows(UnsupportedOperationException.class, () -> manager.generateTeacherSchedule("TCH-SMITH"));

        assertThrows(UnsupportedOperationException.class, () -> ScheduleEntry.calculateTotalDuration(List.of(lesson)));
        assertThrows(UnsupportedOperationException.class, () -> SubjectType.fromString("MATH"));
        assertThrows(UnsupportedOperationException.class, () -> EntryStatus.fromString("SCHEDULED"));
    }
}
