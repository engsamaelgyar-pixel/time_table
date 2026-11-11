package com.school.timetable;

import com.school.timetable.gui.TimetableGUI;
import com.school.timetable.manager.TimetableManager;

import javax.swing.*;

/**
 * Main application launcher for the School Timetable System.
 */
public class Main {

    public static void main(String[] args) {
        // Set modern system look and feel for native OS appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Swing look and feel
        }

        // Launch UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            TimetableManager manager = new TimetableManager();
            TimetableGUI gui = new TimetableGUI(manager);
            gui.setVisible(true);
        });
    }
}
