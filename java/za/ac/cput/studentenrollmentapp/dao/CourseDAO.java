package za.ac.cput.studentenrollmentapp.dao;

import za.ac.cput.studentenrollmentapp.connection.DBConnection;
import za.ac.cput.studentenrollmentapp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO course (course_code, course_name) VALUES (?, ?)";
        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseCode().toUpperCase());
            ps.setString(2, course.getCourseName());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("CourseDAO addCourse error: " + e.getMessage());
            return false;
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course";

        try (Connection conn = DBConnection.derbyConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_code"),
                    rs.getString("course_name")
                );
                courses.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

   public Course getCourseByCode(String courseCode) {
        Course course = null;
        String sql = "SELECT * FROM Course WHERE course_Code = ?";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                course = new Course(
                    rs.getString("course_code"),
                    rs.getString("course_name")
                );
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return course;
    }
}
