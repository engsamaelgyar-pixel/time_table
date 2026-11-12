package com.school.timetable.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleEntry {

    String getItemId();

    DayOfWeek getDay();

    LocalTime getStartTime();

    Duration getDuration();

    SubjectType getSubject();

    EntryStatus getStatus();

    String getScheduledTime();

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
        System.out.println(entries.toString());
        throw new UnsupportedOperationException("Not implemented yet: ScheduleEntry.calculateTotalDuration(List<ScheduleEntry>)");
    }
}
