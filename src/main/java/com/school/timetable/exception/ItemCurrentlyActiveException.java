package com.school.timetable.exception;

/**
 * Custom checked exception thrown when an operation cannot be performed
 * because a timetable item or room is currently active or undergoing a scheduling conflict.
 */
public class ItemCurrentlyActiveException extends Exception {

    public ItemCurrentlyActiveException(String message) {
        super(message);
    }

    public ItemCurrentlyActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
