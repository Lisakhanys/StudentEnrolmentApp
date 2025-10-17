package za.ac.cput.studentenrollmentapp.dao;

import za.ac.cput.studentenrollmentapp.connection.DBConnection;
import za.ac.cput.studentenrollmentapp.domain.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (studentNumber, firstName, lastName, password, email, role) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUser = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement psStudent = conn.prepareStatement(sql);
             PreparedStatement psUser = conn.prepareStatement(sqlUser)) {

            conn.setAutoCommit(false);

            psStudent.setString(1, student.getStudentNumber().toLowerCase());
            psStudent.setString(2, student.getFirstName());
            psStudent.setString(3, student.getLastName());
            psStudent.setString(4, student.getPassword());
            psStudent.setString(5, student.getEmail());
            psStudent.setString(6, student.getRole());
            psStudent.executeUpdate();

            psUser.setString(1, student.getStudentNumber().toLowerCase());
            psUser.setString(2, student.getPassword());
            psUser.setString(3, student.getRole().toLowerCase());
            psUser.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("StudentDAO addStudent error: " + e.getMessage());
            return false;
        }
    }
    
    public List<Student> getStudentsByCourse(String courseCode) {
    List<Student> students = new ArrayList<>();
    String sql = "SELECT s.* FROM students s " +
                "JOIN enrolments e ON s.studentNumber = e.studentNumber " +
                "WHERE e.course_code = ?";
    
    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, courseCode);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Student student = new Student(
                rs.getString("studentNumber"),   
                rs.getString("firstname"),            
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"),
                
                rs.getString("role")
            );
            students.add(student);
        }
        
    } catch (SQLException e) {
        System.out.println("Error getting students by course: " + e.getMessage());
        e.printStackTrace();
    }
    
    return students;
}
}
