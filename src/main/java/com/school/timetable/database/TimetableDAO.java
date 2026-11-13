package com.school.timetable.database;

import com.school.timetable.model.ScheduleEntry;
import com.school.timetable.model.StudentAttendance;

import java.sql.SQLException;
import java.util.List;

public interface TimetableDAO {

    
    void saveLesson(ScheduleEntry entry) throws SQLException;

    void deleteLesson(String itemId) throws SQLException;

    List<ScheduleEntry> findAllLessons() throws SQLException;

    /**
     * Retrieves all classroom lessons assigned to a specific teacher.
     *
     * @param teacherId teacher identifier
     * @return list of matching ClassroomLessons
     * @throws SQLException on database error
     */
    List<ScheduleEntry> findClassroomLessonsByTeacher(String teacherId) throws SQLException;

    /**
     * Records a student attendance entry for an online lesson.
     *
     * @param lessonId   the online lesson item ID
     * @param attendance the attendance record
     * @throws SQLException on database error
     */
    void saveAttendance(String lessonId, StudentAttendance attendance) throws SQLException;
}
