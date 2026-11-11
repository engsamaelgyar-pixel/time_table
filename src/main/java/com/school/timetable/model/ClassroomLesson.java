package com.school.timetable.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Final subclass representing an in-person physical classroom lesson.
 */
public final class ClassroomLesson extends TimetableItem {

    private final LocationData location;
    private final String teacherId;
    private RoomStatus roomStatus;

    public ClassroomLesson(String itemId, Duration duration, DayOfWeek day, LocalTime startTime,
                           SubjectType subject, EntryStatus status, LocalDateTime endDateTime,
                           LocationData location, String teacherId, RoomStatus roomStatus) {
        super(itemId, duration, day, startTime, subject, status, endDateTime);
        this.location = location;
        this.teacherId = teacherId;
        this.roomStatus = roomStatus;
    }

    public LocationData getLocation() {
        return location;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    @Override
    public boolean isAvailable() {
        // TODO: Implement classroom availability logic based on roomStatus, teacher schedule, and room capacity
        throw new UnsupportedOperationException("Not implemented yet: ClassroomLesson.isAvailable()");
    }

    @Override
    public String getSpecifics() {
        // TODO: Implement formatted string returning classroom specifics (e.g. Room, Building, Capacity, Teacher ID)
        throw new UnsupportedOperationException("Not implemented yet: ClassroomLesson.getSpecifics()");
    }

    @Override
    public String toString() {
        String locStr = location != null ? location.roomNumber() : "TBD";
        return String.format("[Classroom] %s | %s | %s %s | Room: %s | Teacher: %s [%s]",
                itemId, subject != null ? subject.getDisplayName() : "N/A",
                day, startTime, locStr, teacherId, status);
    }
}
