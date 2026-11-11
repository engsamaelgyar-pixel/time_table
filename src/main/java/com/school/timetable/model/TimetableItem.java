package com.school.timetable.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Sealed abstract class representing a generic timetable item.
 * Permitted subclasses are strictly limited to {@link ClassroomLesson} and {@link OnlineLesson}.
 */
public abstract sealed class TimetableItem implements ScheduleEntry permits ClassroomLesson, OnlineLesson {

    protected final String itemId;
    protected final Duration duration;
    protected DayOfWeek day;
    protected LocalTime startTime;
    protected final SubjectType subject;
    protected EntryStatus status;
    protected LocalDateTime endDateTime;

    protected static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Protected constructor for sealed hierarchy.
     */
    public TimetableItem(String itemId, Duration duration, DayOfWeek day, LocalTime startTime,
                         SubjectType subject, EntryStatus status, LocalDateTime endDateTime) {
        this.itemId = itemId;
        this.duration = duration;
        this.day = day;
        this.startTime = startTime;
        this.subject = subject;
        this.status = status;
        this.endDateTime = endDateTime;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public SubjectType getSubject() {
        return subject;
    }

    @Override
    public EntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntryStatus status) {
        this.status = status;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * Formats start and end times into a readable range (e.g. "09:00 - 10:00").
     */
    @Override
    public String getScheduledTime() {
        // TODO: Implement formatted time using TIME_FORMATTER (e.g., startTime.format(TIME_FORMATTER) + " - " + calculateEndTime().format(TIME_FORMATTER))
        throw new UnsupportedOperationException("Not implemented yet: TimetableItem.getScheduledTime()");
    }

    /**
     * Checks availability of the item.
     */
    @Override
    public boolean isAvailable() {
        // TODO: Implement availability checking logic
        throw new UnsupportedOperationException("Not implemented yet: TimetableItem.isAvailable()");
    }

    /**
     * Returns type-specific details for UI rendering and reports.
     *
     * @return summary string of specific attributes
     */
    public abstract String getSpecifics();
}
