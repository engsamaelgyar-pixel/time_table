package com.school.timetable.manager;

import com.school.timetable.exception.ItemCurrentlyActiveException;
import com.school.timetable.model.ClassroomLesson;
import com.school.timetable.model.ScheduleEntry;
import com.school.timetable.database.TimetableDAOImpl;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manager class responsible for maintaining and querying school timetable entries.
 * <p>
 * Core domain methods are left as stubs with detailed instructions for implementation.
 */
public class TimetableManager {

    private final List<ScheduleEntry> lessons;


    public TimetableManager() {
        this.lessons = new ArrayList<>();

    }


     //return new ArrayList containing the lessons

    public List<ScheduleEntry> getLessons() throws SQLException, UnsupportedOperationException {
        TimetableDAOImpl data= new TimetableDAOImpl();
       return data.findAllLessons();

    }


     // Adds a new timetable entry after performing validation.

    public void addLesson(ScheduleEntry entry) throws ItemCurrentlyActiveException {
         TimetableDAOImpl data=new TimetableDAOImpl();
        try {

            data.saveLesson(entry);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


     // Removes an existing timetable entry.

    public void removeLesson(ScheduleEntry entry) throws SQLException {
        TimetableDAOImpl data = new TimetableDAOImpl();
        data.deleteLesson(entry.getItemId());

    }

    /**
     * Generates a weekly timetable report grouping entries by DayOfWeek.
     * <p>
     * Recommended implementation uses Java Streams and Collectors:
     * {@code lessons.stream().collect(Collectors.groupingBy(ScheduleEntry::getDay))}
     *
     * @return map grouping entries by DayOfWeek
     */
    public Map<DayOfWeek, List<ScheduleEntry>> generateReport() {
        // TODO: Implement grouping report using Java Streams and method references (Collectors.groupingBy(ScheduleEntry::getDay))
        throw new UnsupportedOperationException("Not implemented yet: TimetableManager.generateReport()");
    }

    /**
     * Generates a schedule of lessons assigned to a specific teacher.
     * <p>
     * Recommended implementation filters for {@link ClassroomLesson} instances matching {@code teacherId}
     * using Java Streams, lambda expressions, and predicates.
     *
     * @param teacherId the teacher identifier
     * @return filtered list of schedule entries
     */
    public List<ScheduleEntry> generateTeacherSchedule(String teacherId) throws SQLException {
        TimetableDAOImpl data =new TimetableDAOImpl();
        try {
            return data.findClassroomLessonsByTeacher(teacherId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
