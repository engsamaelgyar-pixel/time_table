-- =====================================================================
-- School Timetable System - Simple Local MySQL Database Setup
-- Run this entire script in MySQL Workbench, phpMyAdmin (XAMPP), or CMD
-- =====================================================================

-- 1. Create and Select the Database
CREATE DATABASE IF NOT EXISTS school_timetable_db;
USE school_timetable_db;

-- 2. Drop existing tables safely in reverse order
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS student_attendance;
DROP TABLE IF EXISTS online_lessons;
DROP TABLE IF EXISTS classroom_lessons;
DROP TABLE IF EXISTS timetable_items;
DROP TABLE IF EXISTS locations;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- 3. Create Tables
-- =====================================================================

-- Table 1: Classroom Locations
CREATE TABLE locations (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    building VARCHAR(100) NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    capacity INT NOT NULL DEFAULT 30
);

-- Table 2: Base Timetable Items
CREATE TABLE timetable_items (
    item_id VARCHAR(50) PRIMARY KEY,
    item_type ENUM('CLASSROOM', 'ONLINE') NOT NULL,
    subject VARCHAR(50) NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
);

-- Table 3: Classroom Lessons (references timetable_items & locations)
CREATE TABLE classroom_lessons (
    item_id VARCHAR(50) PRIMARY KEY,
    location_id INT NOT NULL,
    teacher_id VARCHAR(50) NOT NULL,
    room_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (item_id) REFERENCES timetable_items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(location_id) ON DELETE CASCADE
);

-- Table 4: Online Lessons (references timetable_items)
CREATE TABLE online_lessons (
    item_id VARCHAR(50) PRIMARY KEY,
    platform VARCHAR(50) NOT NULL,
    meeting_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (item_id) REFERENCES timetable_items(item_id) ON DELETE CASCADE
);

-- Table 5: Student Attendance (references online_lessons)
CREATE TABLE student_attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    lesson_id VARCHAR(50) NOT NULL,
    student_id VARCHAR(50) NOT NULL,
    present BOOLEAN NOT NULL DEFAULT TRUE,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES online_lessons(item_id) ON DELETE CASCADE
);

-- =====================================================================
-- 4. Sample Test Data
-- =====================================================================

-- Locations
INSERT INTO locations (location_id, building, room_number, capacity) VALUES
(1, 'Science Wing', 'Lab 101', 30),
(2, 'Humanities Hall', 'Room 204', 25),
(3, 'Sports Complex', 'Gym A', 50);

-- Timetable Items
INSERT INTO timetable_items (item_id, item_type, subject, day_of_week, start_time, duration_minutes, status) VALUES
('CLS-101', 'CLASSROOM', 'MATH', 'MONDAY', '09:00:00', 60, 'SCHEDULED'),
('CLS-102', 'CLASSROOM', 'ENGLISH', 'TUESDAY', '10:30:00', 45, 'SCHEDULED'),
('ONL-201', 'ONLINE', 'SCIENCE', 'WEDNESDAY', '14:00:00', 45, 'SCHEDULED');

-- Classroom Lessons
INSERT INTO classroom_lessons (item_id, location_id, teacher_id, room_status) VALUES
('CLS-101', 1, 'TCH-SMITH', 'AVAILABLE'),
('CLS-102', 2, 'TCH-JOHNSON', 'AVAILABLE');

-- Online Lessons
INSERT INTO online_lessons (item_id, platform, meeting_url) VALUES
('ONL-201', 'TEAMS', 'https://teams.microsoft.com/meet/123');

-- Student Attendance
INSERT INTO student_attendance (lesson_id, student_id, present) VALUES
('ONL-201', 'STU-101', TRUE),
('ONL-201', 'STU-102', TRUE),
('ONL-201', 'STU-103', FALSE);
