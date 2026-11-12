package com.school.timetable.database;

import com.school.timetable.model.*;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of TimetableDAO providing JDBC operations with MySQL.
 * Employs PreparedStatement, transactions, and modern Java pattern matching.
 */
public class TimetableDAOImpl implements TimetableDAO {

    @Override
    public void saveLesson(ScheduleEntry entry) throws SQLException {
        if (entry == null) return;

        String itemSql = """
            INSERT INTO timetable_items 
                (item_id, item_type, subject, day_of_week, start_time, duration_minutes, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                subject = VALUES(subject),
                day_of_week = VALUES(day_of_week),
                start_time = VALUES(start_time),
                duration_minutes = VALUES(duration_minutes),
                status = VALUES(status)
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String type = (entry instanceof ClassroomLesson) ? "CLASSROOM" : "ONLINE";



                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setString(1, entry.getItemId());
                    ps.setString(2, type);
                    ps.setString(3, entry.getSubject().name());
                    ps.setString(4, entry.getDay().name());
                    ps.setTime(5, Time.valueOf(entry.getStartTime()));
                    ps.setInt(6, (int) entry.getDuration().toMinutes());
                    ps.setString(7, entry.getStatus().name());
                    ps.executeUpdate();
                }

                // Subclass-specific insertion
                if (entry instanceof ClassroomLesson cl) {
                    int locationId = getOrCreateLocation(conn, cl.getLocation());
                    String clSql = """
                        INSERT INTO classroom_lessons (item_id, location_id, teacher_id, room_status)
                        VALUES (?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            location_id = VALUES(location_id),
                            teacher_id = VALUES(teacher_id),
                            room_status = VALUES(room_status)
                        """;
                    try (PreparedStatement ps = conn.prepareStatement(clSql)) {
                        ps.setString(1, cl.getItemId());
                        ps.setInt(2, locationId);
                        ps.setString(3, cl.getTeacherId());
                        ps.setString(4, cl.getRoomStatus() != null ? cl.getRoomStatus().name() : RoomStatus.AVAILABLE.name());
                        ps.executeUpdate();
                    }
                } else if (entry instanceof OnlineLesson ol) {
                    String olSql = """
                        INSERT INTO online_lessons (item_id, platform, meeting_url)
                        VALUES (?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            platform = VALUES(platform),
                            meeting_url = VALUES(meeting_url)
                        """;
                    try (PreparedStatement ps = conn.prepareStatement(olSql)) {
                        ps.setString(1, ol.getItemId());
                        ps.setString(2, ol.getPlatform() != null ? ol.getPlatform().name() : PlatformType.TEAMS.name());
                        ps.setString(3, ol.getMeetingUrl());
                        ps.executeUpdate();
                    }

                    // Save attendance records
                    for (StudentAttendance sa : ol.getAttendanceRecords()) {
                        saveAttendanceInternal(conn, ol.getItemId(), sa);
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void deleteLesson(String itemId) throws SQLException {
        String sql = "DELETE FROM timetable_items WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ScheduleEntry> findAllLessons() throws SQLException {
        List<ScheduleEntry> results = new ArrayList<>();
        results.addAll(findAllClassroomLessons());
        results.addAll(findAllOnlineLessons());
        System.out.println("res is "+results.toString());
        return results;
    }

    @Override
    public List<ClassroomLesson> findClassroomLessonsByTeacher(String teacherId) throws SQLException {
        String sql = """
            SELECT item_id, subject, day_of_week, start_time, duration_minutes, end_datetime, status,
                   teacher_id, room_status, building, room_number, capacity
            FROM vw_classroom_lessons
            WHERE teacher_id = ?
            """;
        List<ClassroomLesson> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapClassroomLesson(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void saveAttendance(String lessonId, StudentAttendance attendance) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            saveAttendanceInternal(conn, lessonId, attendance);
        }
    }

    // -------------------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------------------

    private List<ClassroomLesson> findAllClassroomLessons() throws SQLException {
        String sql = """
            SELECT cl.item_id, ti.subject, ti.day_of_week, ti.start_time, ti.duration_minutes,
                    ti.status, cl.teacher_id, cl.room_status,
                   loc.building, loc.room_number, loc.capacity
            FROM timetable_items ti
            JOIN classroom_lessons cl ON ti.item_id = cl.item_id
            JOIN locations loc ON cl.location_id = loc.location_id
            """;
        List<ClassroomLesson> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapClassroomLesson(rs));
            }
        }
        return list;
    }

    private List<OnlineLesson> findAllOnlineLessons() throws SQLException {
        String sql = """
            SELECT ti.item_id, ti.subject, ti.day_of_week, ti.start_time, ti.duration_minutes, 
                   ti.status, ol.platform, ol.meeting_url
            FROM timetable_items ti
            JOIN online_lessons ol ON ti.item_id = ol.item_id
            """;
        List<OnlineLesson> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                List<StudentAttendance> attendance = loadAttendanceForLesson(conn, itemId);

                SubjectType subject = SubjectType.valueOf(rs.getString("subject"));
                DayOfWeek day = DayOfWeek.valueOf(rs.getString("day_of_week"));
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                Duration duration = Duration.ofMinutes(rs.getInt("duration_minutes"));
                EntryStatus status = EntryStatus.valueOf(rs.getString("status"));
                PlatformType platform = PlatformType.valueOf(rs.getString("platform"));
                String meetingUrl = rs.getString("meeting_url");

                list.add(new OnlineLesson(itemId, duration, day, startTime, subject, status,
                        platform, meetingUrl, attendance));
            }
        }
        return list;
    }

    private ClassroomLesson mapClassroomLesson(ResultSet rs) throws SQLException {
        String itemId = rs.getString("item_id");
        SubjectType subject = SubjectType.valueOf(rs.getString("subject"));
        DayOfWeek day = DayOfWeek.valueOf(rs.getString("day_of_week"));
        LocalTime startTime = rs.getTime("start_time").toLocalTime();
        Duration duration = Duration.ofMinutes(rs.getInt("duration_minutes"));
        EntryStatus status = EntryStatus.valueOf(rs.getString("status"));

        String teacherId = rs.getString("teacher_id");
        RoomStatus roomStatus = RoomStatus.valueOf(rs.getString("room_status"));
        LocationData location = new LocationData(
                rs.getString("building"),
                rs.getString("room_number"),
                String.valueOf(rs.getInt("capacity"))
        );

        return new ClassroomLesson(itemId, duration, day, startTime, subject, status,
                location, teacherId, roomStatus);
    }

    private List<StudentAttendance> loadAttendanceForLesson(Connection conn, String lessonId) throws SQLException {
        String sql = "SELECT student_id, present, timestamp FROM student_attendance WHERE lesson_id = ?";
        List<StudentAttendance> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StudentAttendance(
                            rs.getString("student_id"),
                            rs.getBoolean("present"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    ));
                }
            }
        }
        return list;
    }

    private int getOrCreateLocation(Connection conn, LocationData loc) throws SQLException {
        if (loc == null) return 1;

        String selectSql = "SELECT location_id FROM locations WHERE building = ? AND room_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, loc.building());
            ps.setString(2, loc.roomNumber());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("location_id");
                }
            }
        }

        String insertSql = "INSERT INTO locations (building, room_number, capacity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, loc.building());
            ps.setString(2, loc.roomNumber());
            int cap = 30;
            try { cap = Integer.parseInt(loc.capacity()); } catch (Exception ignored) {}
            ps.setInt(3, cap);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 1;
    }

    private void saveAttendanceInternal(Connection conn, String lessonId, StudentAttendance sa) throws SQLException {
        String sql = """
            INSERT INTO student_attendance (lesson_id, student_id, present, timestamp)
            VALUES (?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lessonId);
            ps.setString(2, sa.studentId());
            ps.setBoolean(3, sa.present());
            ps.setTimestamp(4, Timestamp.valueOf(sa.timestamp() != null ? sa.timestamp() : LocalDateTime.now()));
            ps.executeUpdate();
        }
    }
}
