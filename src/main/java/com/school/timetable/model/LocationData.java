package com.school.timetable.model;

/**
 * Immutable record representing physical classroom location details.
 *
 * @param building   the building name or identifier (e.g. "Science Block")
 * @param roomNumber the classroom room number (e.g. "Lab 101")
 * @param capacity   the seating capacity of the room (e.g. "30")
 */
public record LocationData(String building, String roomNumber, String capacity) {

    /**
     * Convenience constructor accepting roomNumber and capacity, defaulting building.
     *
     * @param roomNumber room identifier
     * @param capacity   seating capacity
     */
    public LocationData(String roomNumber, String capacity) {
        this("Main Campus", roomNumber, capacity);
    }

    /**
     * Compact constructor for data validation.
     */
    public LocationData {
        // TODO: Implement validation rules (e.g., non-null, positive capacity check)
    }

    /**
     * Formats location details for display.
     *
     * @return formatted location string
     */
    public String getFormattedLocation() {
        // TODO: Implement formatted location string
        throw new UnsupportedOperationException("Not implemented yet: LocationData.getFormattedLocation()");
    }
}
