package com.school.timetable.model;

/**
 * Enumeration representing distinct school subjects.
 * Provides user-friendly display names and conversion utility stubs.
 */
public enum SubjectType {
    MATH("Mathematics"),
    SCIENCE("Science"),
    ENGLISH("English"),
    HISTORY("History"),
    ART("Art"),
    PHYSICAL_EDUCATION("Physical Education"),
    TEAMS("Team Sports");

    private final String displayName;

    SubjectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a string representation (enum name or display name) to a SubjectType.
     *
     * @param text input string to parse
     * @return matching SubjectType
     * @throws IllegalArgumentException if no matching subject is found
     * @throws UnsupportedOperationException when invoked as a stub
     */
    public static SubjectType fromString(String text) {
        // TODO: Implement conversion utility matching text against enum names or displayNames (case-insensitive)
        throw new UnsupportedOperationException("Not implemented yet: SubjectType.fromString(String text)");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
