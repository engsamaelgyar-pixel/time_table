package com.school.timetable.model;

/**
 * Enumeration representing the scheduling status of a timetable entry.
 */
public enum EntryStatus {
    SCHEDULED("Scheduled"),
    RESCHEDULED("Rescheduled"),
    CANCELLED("Cancelled");

    private final String displayName;

    EntryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a string representation to an EntryStatus.
     *
     * @param text input text
     * @return matching EntryStatus
     */
    public static EntryStatus fromString(String text) {
        // TODO: Implement conversion utility matching text against EntryStatus values
        throw new UnsupportedOperationException("Not implemented yet: EntryStatus.fromString(String text)");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
