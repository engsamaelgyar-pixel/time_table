package com.school.timetable.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

/**
 * Core interface defining the contract for any scheduled timetable entry.
 * Demonstrates abstract methods, default methods with private helper methods (Java 9+),
 * and static utility methods.
 */
public interface ScheduleEntry {

    /**
     * @return unique identifier of the timetable item
     */
    String getItemId();

    /**
     * @return the day of week when the entry occurs
     */
    DayOfWeek getDay();

    /**
     * @return the starting time of the entry
     */
    LocalTime getStartTime();

    /**
     * @return the duration of the entry
     */
    Duration getDuration();

    /**
     * @return the subject being taught
     */
    SubjectType getSubject();

    /**
     * @return current status of the schedule entry
     */
    EntryStatus getStatus();

    /**
     * Abstract method returning formatted scheduled time span (e.g. "09:00 - 10:00").
     *
     * @return formatted time range string
     */
    String getScheduledTime();

    /**
     * Abstract method checking whether the scheduled item is currently available or valid.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Default method to validate the entry.
     * Delegates internally to a private interface method.
     */
    default void validate() {
        // TODO: Implement schedule validation logic
        performFormatCheck();
        throw new UnsupportedOperationException("Not implemented yet: ScheduleEntry.validate()");
    }

    /**
     * Private helper method in interface (Modern Java feature) supporting default method logic.
     */
    private void performFormatCheck() {
        // TODO: Implement private format checking logic
        throw new UnsupportedOperationException("Not implemented yet: ScheduleEntry.performFormatCheck()");
    }

    /**
     * Static utility method to sum the durations of a list of ScheduleEntry items.
     *
     * @param entries list of schedule entries
     * @return total aggregated Duration
     */
    static Duration calculateTotalDuration(List<ScheduleEntry> entries) {
        // TODO: Implement total duration calculation using Java Streams and Duration::plus
        throw new UnsupportedOperationException("Not implemented yet: ScheduleEntry.calculateTotalDuration(List<ScheduleEntry>)");
    }
}
