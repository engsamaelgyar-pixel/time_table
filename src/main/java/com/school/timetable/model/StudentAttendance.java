package com.school.timetable.model;

import java.time.LocalDateTime;

/**
 * Immutable record representing a single student's attendance record for an online lesson.
 *
 * @param studentId the unique student identifier
 * @param present   true if the student attended, false otherwise
 * @param timestamp the timestamp when attendance was marked
 */
public record StudentAttendance(String studentId, boolean present, LocalDateTime timestamp) {

    /**
     * Compact constructor for data validation.
     */
    public StudentAttendance {
        // TODO: Implement validation rules (e.g., studentId cannot be null or blank, timestamp cannot be null)
    }
}
