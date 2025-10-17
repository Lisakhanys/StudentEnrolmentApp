package za.ac.cput.studentenrollmentapp.dao;

import za.ac.cput.studentenrollmentapp.connection.DBConnection;
import za.ac.cput.studentenrollmentapp.domain.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import za.ac.cput.studentenrollmentapp.domain.Student;

public class EnrollmentDAO {

    public boolean enrolStudent(String studentNumber, String courseCode) {
        String sql = "INSERT INTO enrolment (studentNumber, course_code) VALUES (?, ?)";
        try (Connection conn = DBConnection.derbyConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentNumber.toLowerCase());
            ps.setString(2, courseCode.toUpperCase());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("EnrollmentDAO enrolStudent error: " + e.getMessage());
            return false;
        }
    }

    public List<Course> getCoursesForStudent(String studentNumber) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM course c "
                + "JOIN enrolment e ON c.course_code = e.course_code "
                + "WHERE e.studentNumber = ?";

        try (Connection conn = DBConnection.derbyConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getString("course_code"),
                        rs.getString("course_name")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error getting courses for student: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

   public List<Student> getStudentsByCourse(String courseCode) {
    List<Student> students = new ArrayList<>();

    String sql = "SELECT s.studentNumber, s.firstName, s.lastName,s.password,s.email, s.role " +
                 "FROM students s " +
                 "JOIN enrolment e ON s.studentNumber = e.studentNumber " +
                 "WHERE e.course_code = ?";

    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, courseCode.toUpperCase());
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Student s = new Student(
                rs.getString("studentNumber"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("password"),
                rs.getString("email"),
                
                rs.getString("role")
            );
            students.add(s);
        }

        rs.close();
    } catch (SQLException e) {
        System.out.println("Error getting students by course: " + e.getMessage());
        e.printStackTrace();
    }

    return students;
}

}
