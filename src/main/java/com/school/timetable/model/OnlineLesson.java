package com.school.timetable.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Final subclass representing a virtual remote online lesson.
 */
public final class OnlineLesson extends TimetableItem {

    private final PlatformType platform;
    private final String meetingUrl;
    private final List<StudentAttendance> attendanceRecords;

    public OnlineLesson(String itemId, Duration duration, DayOfWeek day, LocalTime startTime,
                        SubjectType subject, EntryStatus status,
                        PlatformType platform, String meetingUrl, List<StudentAttendance> attendanceRecords) {
        super(itemId, duration, day, startTime, subject, status);
        this.platform = platform;
        this.meetingUrl = meetingUrl;
        this.attendanceRecords = attendanceRecords != null ? new ArrayList<>(attendanceRecords) : new ArrayList<>();
    }

    public PlatformType getPlatform() {
        return platform;
    }

    public String getMeetingPlatform() {
        return platform != null ? platform.getDisplayName() : "N/A";
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    /**
     * Returns defensive copy of attendance records.
     */
    public List<StudentAttendance> getAttendanceRecords() {
        return new ArrayList<>(attendanceRecords);
    }

    /**
     * Adds an attendance record.
     */
    public void addAttendance(StudentAttendance attendance) {
        // TODO: Implement attendance registration and validation
        throw new UnsupportedOperationException("Not implemented yet: OnlineLesson.addAttendance(StudentAttendance)");
    }

    @Override
    public boolean isAvailable() {
        // TODO: Implement online lesson platform check and link verification
        throw new UnsupportedOperationException("Not implemented yet: OnlineLesson.isAvailable()");
    }

    @Override
    public String getSpecifics() {
        // TODO: Implement formatted string returning online specifics (Platform, Meeting URL, Total Attendance)
        throw new UnsupportedOperationException("Not implemented yet: OnlineLesson.getSpecifics()");
    }

    @Override
    public String toString() {
        String platStr = platform != null ? platform.getDisplayName() : "Online";
        return String.format("[Online] %s | %s | %s %s | Platform: %s | URL: %s [%s]",
                itemId, subject != null ? subject.getDisplayName() : "N/A",
                day, startTime, platStr, meetingUrl, status);
    }
}
