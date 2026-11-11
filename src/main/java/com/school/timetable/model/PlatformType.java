package com.school.timetable.model;

/**
 * Enumeration representing supported virtual meeting platforms for online lessons.
 */
public enum PlatformType {
    ZOOM("Zoom"),
    TEAMS("Microsoft Teams"),
    GOOGLE_MEET("Google Meet"),
    WEBEX("Cisco Webex");

    private final String displayName;

    PlatformType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
