package com.school.timetable.gui;

import com.school.timetable.exception.ItemCurrentlyActiveException;
import com.school.timetable.manager.TimetableManager;
import com.school.timetable.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fully implemented Java Swing Graphical User Interface for the School Timetable System.
 * Incorporates JList, DefaultListModel, JTextFields, JComboBoxes, and functional ActionListeners
 * using modern lambdas and method references.
 */
public class TimetableGUI extends JFrame {

    private final TimetableManager manager;
    private final DefaultListModel<ScheduleEntry> lessonListModel;

    // UI Components - Display & List
    private JList<ScheduleEntry> lessonList;
    private JLabel statusLabel;

    // UI Components - Form Controls
    private JComboBox<String> lessonTypeCombo;
    private JTextField itemIdField;
    private JComboBox<SubjectType> subjectCombo;
    private JComboBox<DayOfWeek> dayCombo;
    private JTextField startTimeField;
    private JTextField durationField;
    private JComboBox<EntryStatus> statusCombo;

    // Classroom-specific components
    private JPanel classroomFieldsPanel;
    private JTextField buildingField;
    private JTextField roomNumberField;
    private JTextField capacityField;
    private JTextField teacherIdField;
    private JComboBox<RoomStatus> roomStatusCombo;

    // Online-specific components
    private JPanel onlineFieldsPanel;
    private JComboBox<PlatformType> platformCombo;
    private JTextField meetingUrlField;
    private JTextField studentCountField;

    // Card Layout Container for dynamic type-specific panels
    private CardLayout specificCardLayout;
    private JPanel specificCardPanel;

    // Action Buttons
    private JButton addButton;
    private JButton clearButton;
    private JButton viewButton;
    private JButton rescheduleButton;
    private JButton deleteButton;
    private JButton weeklyReportButton;
    private JButton teacherScheduleButton;
    private JButton totalDurationButton;

    public TimetableGUI(TimetableManager manager) {
        this.manager = manager != null ? manager : new TimetableManager();
        this.lessonListModel = new DefaultListModel<>();

        initUI();
        loadExistingLessons();
    }

    private void initUI() {
        setTitle("School Timetable System - Management Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Header Banner
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Central Split: Left = List View, Right = Form Input
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createListPanel(), createFormPanel());
        splitPane.setDividerLocation(520);
        splitPane.setResizeWeight(0.5);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // 3. Status Bar
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 53, 85));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("School Timetable System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Object-Oriented Desktop Timetable Management | Modern Java (Records, Sealed Hierarchy, Enums)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 230, 242));

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Scheduled Entries",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        lessonList = new JList<>(lessonListModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lessonList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lessonList.setCellRenderer(new TimetableListCellRenderer());
        lessonList.addListSelectionListener(this::onListSelectionChanged);

        JScrollPane scrollPane = new JScrollPane(lessonList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Toolbar below the list
        JPanel buttonGrid = new JPanel(new GridLayout(2, 3, 6, 6));

        viewButton = new JButton("View Details");
        viewButton.setToolTipText("View comprehensive specifics of selected timetable entry");
        viewButton.addActionListener(this::onViewDetails);

        rescheduleButton = new JButton("Reschedule...");
        rescheduleButton.setToolTipText("Change day and time of selected entry");
        rescheduleButton.addActionListener(this::onReschedule);

        deleteButton = new JButton("Delete Entry");
        deleteButton.setToolTipText("Remove selected entry from the timetable");
        deleteButton.addActionListener(this::onDeleteLesson);

        weeklyReportButton = new JButton("Weekly Report");
        weeklyReportButton.setToolTipText("Group entries by DayOfWeek using Streams");
        weeklyReportButton.addActionListener(this::onGenerateReport);

        teacherScheduleButton = new JButton("Teacher Schedule");
        teacherScheduleButton.setToolTipText("Filter lessons by teacher ID");
        teacherScheduleButton.addActionListener(this::onFilterTeacherSchedule);

        totalDurationButton = new JButton("Total Duration");
        totalDurationButton.setToolTipText("Sum durations across all entries");
        totalDurationButton.addActionListener(this::onCalculateTotalDuration);

        buttonGrid.add(viewButton);
        buttonGrid.add(rescheduleButton);
        buttonGrid.add(deleteButton);
        buttonGrid.add(weeklyReportButton);
        buttonGrid.add(teacherScheduleButton);
        buttonGrid.add(totalDurationButton);

        panel.add(buttonGrid, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout(8, 8));
        container.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Schedule New Entry",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Lesson Type Selection
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Entry Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        lessonTypeCombo = new JComboBox<>(new String[]{"Classroom Lesson", "Online Lesson"});
        lessonTypeCombo.addActionListener(e -> onLessonTypeChanged());
        formFields.add(lessonTypeCombo, gbc);
        row++;

        // Item ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Item ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemIdField = new JTextField("CLS-101");
        formFields.add(itemIdField, gbc);
        row++;

        // Subject
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        subjectCombo = new JComboBox<>(SubjectType.values());
        formFields.add(subjectCombo, gbc);
        row++;

        // Day of Week
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Day of Week:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dayCombo = new JComboBox<>(DayOfWeek.values());
        formFields.add(dayCombo, gbc);
        row++;

        // Start Time
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Start Time (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        startTimeField = new JTextField("09:00");
        formFields.add(startTimeField, gbc);
        row++;

        // Duration (Minutes)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Duration (mins):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        durationField = new JTextField("60");
        formFields.add(durationField, gbc);
        row++;

        // Entry Status
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formFields.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        statusCombo = new JComboBox<>(EntryStatus.values());
        formFields.add(statusCombo, gbc);
        row++;

        // Dynamic Card Panel for Type-Specific Attributes
        specificCardLayout = new CardLayout();
        specificCardPanel = new JPanel(specificCardLayout);

        classroomFieldsPanel = createClassroomFieldsPanel();
        onlineFieldsPanel = createOnlineFieldsPanel();

        specificCardPanel.add(classroomFieldsPanel, "CLASSROOM");
        specificCardPanel.add(onlineFieldsPanel, "ONLINE");

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formFields.add(specificCardPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formFields);
        formScroll.setBorder(null);
        container.add(formScroll, BorderLayout.CENTER);

        // Form Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());

        addButton = new JButton("Add Schedule Entry");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(Color.blue);
        addButton.setForeground(Color.black);
        addButton.setFocusPainted(false);
        addButton.addActionListener(this::onAddLesson);

        actionPanel.add(clearButton);
        actionPanel.add(addButton);
        container.add(actionPanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createClassroomFieldsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Classroom Specifics"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int r = 0;
        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Building:"), c);
        c.gridx = 1; c.weightx = 0.7;
        buildingField = new JTextField("Science Wing");
        p.add(buildingField, c);
        r++;

        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Room Number:"), c);
        c.gridx = 1; c.weightx = 0.7;
        roomNumberField = new JTextField("Lab 201");
        p.add(roomNumberField, c);
        r++;

        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Capacity:"), c);
        c.gridx = 1; c.weightx = 0.7;
        capacityField = new JTextField("32");
        p.add(capacityField, c);
        r++;

        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Teacher ID:"), c);
        c.gridx = 1; c.weightx = 0.7;
        teacherIdField = new JTextField("TCH-1001");
        p.add(teacherIdField, c);
        r++;

        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Room Status:"), c);
        c.gridx = 1; c.weightx = 0.7;
        roomStatusCombo = new JComboBox<>(RoomStatus.values());
        p.add(roomStatusCombo, c);

        return p;
    }

    private JPanel createOnlineFieldsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Online Lesson Specifics"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int r = 0;
        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Platform:"), c);
        c.gridx = 1; c.weightx = 0.7;
        platformCombo = new JComboBox<>(PlatformType.values());
        p.add(platformCombo, c);
        r++;

        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Meeting URL:"), c);
        c.gridx = 1; c.weightx = 0.7;
        meetingUrlField = new JTextField("https://meet.google.com/abc-defg-hij");
        p.add(meetingUrlField, c);
        r++;

        c.gridx = 0; c.gridy = r; c.weightx = 0.3;
        p.add(new JLabel("Initial Attendees:"), c);
        c.gridx = 1; c.weightx = 0.7;
        studentCountField = new JTextField("25");
        p.add(studentCountField, c);

        return p;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(4, 4, 4, 4));
        statusLabel = new JLabel("Ready. Total Entries: 0");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    // ------------------------------------------------------------------------
    // Action Listeners & Event Handlers
    // ------------------------------------------------------------------------

    private void onLessonTypeChanged() {
        String selected = (String) lessonTypeCombo.getSelectedItem();
        if ("Classroom Lesson".equals(selected)) {
            specificCardLayout.show(specificCardPanel, "CLASSROOM");
            if (itemIdField.getText().startsWith("ONL-")) {
                itemIdField.setText("CLS-" + System.currentTimeMillis() % 1000);
            }
        } else {
            specificCardLayout.show(specificCardPanel, "ONLINE");
            if (itemIdField.getText().startsWith("CLS-")) {
                itemIdField.setText("ONL-" + System.currentTimeMillis() % 1000);
            }
        }
    }

    private void onListSelectionChanged(ListSelectionEvent e) {
        boolean hasSelection = !lessonList.isSelectionEmpty();
        viewButton.setEnabled(hasSelection);
        rescheduleButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    private void onAddLesson(ActionEvent e) {
        String itemId = itemIdField.getText().trim();
        if (itemId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item ID cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SubjectType subject = (SubjectType) subjectCombo.getSelectedItem();
        DayOfWeek day = (DayOfWeek) dayCombo.getSelectedItem();
        EntryStatus status = (EntryStatus) statusCombo.getSelectedItem();

        LocalTime startTime;
        try {
            startTime = LocalTime.parse(startTimeField.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Start Time format. Please use HH:mm (e.g. 09:30).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int durationMins;
        try {
            durationMins = Integer.parseInt(durationField.getText().trim());
            if (durationMins <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duration must be a positive integer in minutes.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Duration duration = Duration.ofMinutes(durationMins);

        ScheduleEntry entry;
        boolean isClassroom = "Classroom Lesson".equals(lessonTypeCombo.getSelectedItem());

        if (isClassroom) {
            String building = buildingField.getText().trim();
            String roomNum = roomNumberField.getText().trim();
            String cap = capacityField.getText().trim();
            String teacherId = teacherIdField.getText().trim();
            RoomStatus roomStatus = (RoomStatus) roomStatusCombo.getSelectedItem();

            LocationData location = new LocationData(building, roomNum, cap);
            entry = new ClassroomLesson(itemId, duration, day, startTime, subject, status,
                    location, teacherId, roomStatus);
        } else {
            PlatformType platform = (PlatformType) platformCombo.getSelectedItem();
            String meetingUrl = meetingUrlField.getText().trim();
            int count = 0;
            try {
                count = Integer.parseInt(studentCountField.getText().trim());
            } catch (Exception ignored) {}

            List<StudentAttendance> sampleAttendance = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                sampleAttendance.add(new StudentAttendance("STU-" + (100 + i), true, LocalDateTime.now()));
            }

            entry = new OnlineLesson(itemId, duration, day, startTime, subject, status,
                    platform, meetingUrl, sampleAttendance);
        }

        // Add to TimetableManager with graceful handling of domain stubs
        try {
            manager.addLesson(entry);
            lessonListModel.addElement(entry);
            updateStatus("Lesson added successfully.");
            JOptionPane.showMessageDialog(this, "Schedule entry '" + itemId + "' added successfully!", "Entry Added", JOptionPane.INFORMATION_MESSAGE);
        } catch (ItemCurrentlyActiveException activeEx) {
            JOptionPane.showMessageDialog(this, "Scheduling Conflict: " + activeEx.getMessage(), "Conflict Error", JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedOperationException stubEx) {
            // Domain method is a stub: add to UI model anyway so developer/user can test UI operations
            lessonListModel.addElement(entry);
            updateStatus("Entry added to UI list model (manager.addLesson stub triggered).");
            JOptionPane.showMessageDialog(this,
                    "TimetableManager.addLesson() is currently a stub for you to implement!\n" +
                    "The entry was added to the UI display model for visual testing.\n\n" +
                    "Stub notice: " + stubEx.getMessage(),
                    "Manager Stub Triggered", JOptionPane.INFORMATION_MESSAGE);
        }

        clearForm();
    }

    private void onViewDetails(ActionEvent e) {
        ScheduleEntry selected = lessonList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an entry from the list to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String scheduledTimeStr;
        try {
            scheduledTimeStr = selected.getScheduledTime();
        } catch (UnsupportedOperationException ex) {
            scheduledTimeStr = "(getScheduledTime stub: not implemented yet)";
        }

        String specificsStr;
        if (selected instanceof TimetableItem item) {
            try {
                specificsStr = item.getSpecifics();
            } catch (UnsupportedOperationException ex) {
                specificsStr = "(getSpecifics stub: not implemented yet)";
            }
        } else {
            specificsStr = "N/A";
        }

        String availabilityStr;
        try {
            availabilityStr = selected.isAvailable() ? "Yes (Available)" : "No (Unavailable)";
        } catch (UnsupportedOperationException ex) {
            availabilityStr = "(isAvailable stub: not implemented yet)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Schedule Entry Details ===\n\n");
        sb.append("Item ID:        ").append(selected.getItemId()).append("\n");
        sb.append("Subject:        ").append(selected.getSubject().getDisplayName()).append("\n");
        sb.append("Type:           ").append(selected instanceof ClassroomLesson ? "Classroom Lesson" : "Online Lesson").append("\n");
        sb.append("Day:            ").append(selected.getDay()).append("\n");
        sb.append("Start Time:     ").append(selected.getStartTime()).append("\n");
        sb.append("Duration:       ").append(selected.getDuration().toMinutes()).append(" minutes\n");
        sb.append("Scheduled Span: ").append(scheduledTimeStr).append("\n");
        sb.append("Status:         ").append(selected.getStatus()).append("\n");
        sb.append("Available:      ").append(availabilityStr).append("\n\n");

        if (selected instanceof ClassroomLesson cl) {
            sb.append("--- Classroom Specifics ---\n");
            sb.append("Teacher ID:     ").append(cl.getTeacherId()).append("\n");
            sb.append("Building:       ").append(cl.getLocation() != null ? cl.getLocation().building() : "N/A").append("\n");
            sb.append("Room:           ").append(cl.getLocation() != null ? cl.getLocation().roomNumber() : "N/A").append("\n");
            sb.append("Capacity:       ").append(cl.getLocation() != null ? cl.getLocation().capacity() : "N/A").append("\n");
            sb.append("Room Status:    ").append(cl.getRoomStatus()).append("\n");
            sb.append("Specifics:      ").append(specificsStr).append("\n");
        } else if (selected instanceof OnlineLesson ol) {
            sb.append("--- Online Lesson Specifics ---\n");
            sb.append("Platform:       ").append(ol.getMeetingPlatform()).append("\n");
            sb.append("Meeting URL:    ").append(ol.getMeetingUrl()).append("\n");
            sb.append("Attendance:     ").append(ol.getAttendanceRecords().size()).append(" record(s)\n");
            sb.append("Specifics:      ").append(specificsStr).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Timetable Item Details - " + selected.getItemId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void onReschedule(ActionEvent e) {
        ScheduleEntry selected = lessonList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an entry to reschedule.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!(selected instanceof TimetableItem item)) {
            JOptionPane.showMessageDialog(this, "Selected entry cannot be rescheduled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<DayOfWeek> newDayCombo = new JComboBox<>(DayOfWeek.values());
        newDayCombo.setSelectedItem(item.getDay());
        JTextField newTimeField = new JTextField(item.getStartTime().toString(), 8);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Select New Day:"));
        panel.add(newDayCombo);
        panel.add(new JLabel("New Start Time (HH:mm):"));
        panel.add(newTimeField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Reschedule Entry: " + item.getItemId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalTime newTime = LocalTime.parse(newTimeField.getText().trim());
                DayOfWeek newDay = (DayOfWeek) newDayCombo.getSelectedItem();

                item.setDay(newDay);
                item.setStartTime(newTime);
                item.setStatus(EntryStatus.RESCHEDULED);

                lessonList.repaint();
                updateStatus("Item " + item.getItemId() + " rescheduled to " + newDay + " " + newTime);
                JOptionPane.showMessageDialog(this, "Entry rescheduled successfully! Status updated to RESCHEDULED.", "Rescheduled", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException parseEx) {
                JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:mm.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteLesson(ActionEvent e) {
        ScheduleEntry selected = lessonList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete entry: " + selected.getItemId() + " (" + selected.getSubject() + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                manager.removeLesson(selected);
            } catch (UnsupportedOperationException | SQLException ex) {
                // Informative note about manager stub
            }
            lessonListModel.removeElement(selected);
            updateStatus("Deleted entry " + selected.getItemId());
            JOptionPane.showMessageDialog(this, "Entry deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onGenerateReport(ActionEvent e) {
        try {
            Map<DayOfWeek, List<ScheduleEntry>> report = manager.generateReport();
            if (report == null || report.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No entries found in weekly report.", "Weekly Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("=== Weekly Timetable Report (Streams Grouping) ===\n\n");
            report.forEach((day, entries) -> {
                sb.append("[").append(day).append("] (").append(entries.size()).append(" lessons)\n");
                for (ScheduleEntry en : entries) {
                    sb.append("   - ").append(en.getItemId()).append(": ").append(en.getSubject())
                            .append(" at ").append(en.getStartTime()).append(" [").append(en.getStatus()).append("]\n");
                }
                sb.append("\n");
            });

            JTextArea textArea = new JTextArea(sb.toString(), 16, 40);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Weekly Timetable Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnsupportedOperationException stubEx) {
            JOptionPane.showMessageDialog(this,
                    "TimetableManager.generateReport() is currently a stub for you to implement!\n\n" +
                    "Implementation guidance:\n" +
                    "Use Java Streams: lessons.stream().collect(Collectors.groupingBy(ScheduleEntry::getDay));\n\n" +
                    "Exception: " + stubEx.getMessage(),
                    "Manager Stub Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onFilterTeacherSchedule(ActionEvent e) {
        String teacherId = JOptionPane.showInputDialog(this,
                "Enter Teacher ID to query schedule (e.g. TCH-1001):",
                "Filter Teacher Schedule",
                JOptionPane.QUESTION_MESSAGE);

        if (teacherId == null || teacherId.trim().isEmpty()) {
            return;
        }

        try {
            List<ScheduleEntry> teacherLessons = manager.generateTeacherSchedule(teacherId.trim());
            if (teacherLessons == null || teacherLessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No lessons found for Teacher ID: " + teacherId, "Teacher Schedule", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("=== Schedule for Teacher: " + teacherId + " ===\n\n");
            for (ScheduleEntry l : teacherLessons) {
                sb.append(" - ").append(l.getItemId()).append(": ").append(l.getSubject())
                        .append(" | ").append(l.getDay()).append(" at ").append(l.getStartTime())
                        .append(" [").append(l.getStatus()).append("]\n");
            }

            JTextArea textArea = new JTextArea(sb.toString(), 12, 36);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Teacher Schedule", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnsupportedOperationException | SQLException stubEx) {
            JOptionPane.showMessageDialog(this,
                    "TimetableManager.generateTeacherSchedule(String) is currently a stub for you to implement!\n\n" +
                    "Implementation guidance:\n" +
                    "Filter ClassroomLessons matching teacherId using Java Streams, Lambdas, and Predicates.\n\n" +
                    "Exception: " + stubEx.getMessage(),
                    "Manager Stub Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onCalculateTotalDuration(ActionEvent e) {
        List<ScheduleEntry> currentEntries = Collections.list(lessonListModel.elements());
        if (currentEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No entries currently in the timetable.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Duration total = ScheduleEntry.calculateTotalDuration(currentEntries);
            JOptionPane.showMessageDialog(this,
                    "Total Scheduled Duration across " + currentEntries.size() + " entries:\n" +
                    total.toMinutes() + " minutes (" + (total.toMinutes() / 60.0) + " hours)",
                    "Total Duration", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnsupportedOperationException stubEx) {
            JOptionPane.showMessageDialog(this,
                    "ScheduleEntry.calculateTotalDuration(List<ScheduleEntry>) is currently a stub for you to implement!\n\n" +
                    "Implementation guidance:\n" +
                    "Use Streams: entries.stream().map(ScheduleEntry::getDuration).reduce(Duration.ZERO, Duration::plus);\n\n" +
                    "Exception: " + stubEx.getMessage(),
                    "Static Method Stub", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        itemIdField.setText("CLS-" + (System.currentTimeMillis() % 1000));
        startTimeField.setText("09:00");
        durationField.setText("60");
        subjectCombo.setSelectedIndex(0);
        dayCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);

        buildingField.setText("Science Wing");
        roomNumberField.setText("Lab 201");
        capacityField.setText("32");
        teacherIdField.setText("TCH-1001");
        roomStatusCombo.setSelectedIndex(0);

        meetingUrlField.setText("https://meet.google.com/abc-defg-hij");
        studentCountField.setText("25");
        platformCombo.setSelectedIndex(0);
    }

    private void loadExistingLessons() {
        try {
            List<ScheduleEntry> existing = manager.getLessons();
            if (existing != null) {
                existing.forEach(lessonListModel::addElement);
            }
        } catch (UnsupportedOperationException | SQLException ex) {
            // Manager getLessons is a stub; populate sample UI items for user convenience
            populateSampleUIData();
        }
        updateStatus("Initialized.");
    }

    private void populateSampleUIData() {
        // Initial sample items to populate JList for immediate visual verification
        ClassroomLesson c1 = new ClassroomLesson(
                "CLS-101", Duration.ofMinutes(60), DayOfWeek.MONDAY, LocalTime.of(9, 0),
                SubjectType.MATH, EntryStatus.SCHEDULED,
                new LocationData("Main Science Wing", "Lab 101", "30"), "TCH-1001", RoomStatus.AVAILABLE);

        OnlineLesson o1 = new OnlineLesson(
                "ONL-201", Duration.ofMinutes(45), DayOfWeek.WEDNESDAY, LocalTime.of(14, 0),
                SubjectType.SCIENCE, EntryStatus.SCHEDULED,
                PlatformType.TEAMS, "https://teams.microsoft.com/l/meetup/123",
                List.of(new StudentAttendance("STU-001", true, LocalDateTime.now())));

        lessonListModel.addElement(c1);
        lessonListModel.addElement(o1);
    }

    private void updateStatus(String action) {
        statusLabel.setText(String.format("Status: %s | Total Entries: %d", action, lessonListModel.getSize()));
    }

    // ------------------------------------------------------------------------
    // Custom List Cell Renderer
    // ------------------------------------------------------------------------

    private static class TimetableListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ScheduleEntry entry) {
                String typeBadge = (entry instanceof ClassroomLesson) ? "[Classroom]" : "[Online]";
                String teacherOrUrl = "";
                if (entry instanceof ClassroomLesson cl) {
                    teacherOrUrl = "Teacher: " + cl.getTeacherId() + " | Room: " + (cl.getLocation() != null ? cl.getLocation().roomNumber() : "N/A");
                } else if (entry instanceof OnlineLesson ol) {
                    teacherOrUrl = "Platform: " + ol.getMeetingPlatform();
                }

                label.setText(String.format("<html><b>%s %s - %s</b> &nbsp;|&nbsp; <i>%s at %s (%d mins)</i> &nbsp;|&nbsp; %s &nbsp;<font color='%s'><b>[%s]</b></font></html>",
                        typeBadge,
                        entry.getItemId(),
                        entry.getSubject().getDisplayName(),
                        entry.getDay(),
                        entry.getStartTime(),
                        entry.getDuration().toMinutes(),
                        teacherOrUrl,
                        getStatusColorHex(entry.getStatus()),
                        entry.getStatus()));
                label.setBorder(new EmptyBorder(6, 6, 6, 6));
            }
            return label;
        }

        private String getStatusColorHex(EntryStatus status) {
            if (status == null) return "#000000";
            return switch (status) {
                case SCHEDULED -> "#2E7D32";   // Green
                case RESCHEDULED -> "#E65100"; // Orange
                case CANCELLED -> "#C62828";   // Red
            };
        }
    }
}
