package za.ac.cput.studentenrollmentapp;

import za.ac.cput.studentenrollmentapp.dao.*;
import za.ac.cput.studentenrollmentapp.domain.Student;
import za.ac.cput.studentenrollmentapp.domain.Course;
import za.ac.cput.studentenrollmentapp.connection.DBConnection;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 220239215
 */
public class StudentEnrollmentApp {

    private UserDAO userDAO;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;

    public StudentEnrollmentApp() throws SQLException {
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("Student Enrollment Server Starting ");

        // Test DB connection - USING YOUR derbyConnection() METHOD
        try (Connection conn = DBConnection.derbyConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection FAILED! " + e.getMessage());
            e.printStackTrace();
            return;
        }

        new StudentEnrollmentApp().startServer();
    }

    public void startServer() {
        //start server
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started on port 12345. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        //handleclient
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream()); 
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            while (true) {
                String command = (String) in.readObject();
                System.out.println("Received command: " + command);
                
                switch (command) {
                    case "LOGIN":
                        handleLogin(in, out);
                        break;
                    case "ADD_STUDENT":
                        handleAddStudent(in, out);
                        break;
                    case "ADD_COURSE":
                        handleAddCourse(in, out);
                        break;
                    case "GET_STUDENTS_BY_COURSE":
                        handleGetStudentsByCourse(in, out);
                        break;
                    case "GET_COURSES_BY_STUDENT":
                        handleGetCoursesByStudent(in, out);
                        break;
                    case "GET_COURSES":
                        handleGetCourses(out);
                        break;
                    case "ENROL_STUDENT":
                        handleEnrolStudent(in, out);
                        break;
                    case "GET_STUDENT_COURSES":
                        handleGetStudentCourses(in, out);
                        break;
                    case "EXIT":
                        System.out.println("Client requested exit");
                        return;
                    default:
                        out.writeBoolean(false);
                        out.flush();
                        System.out.println("Unknown command: " + command);
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected or error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogin(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        //handle log in credetials
        String username = ((String) in.readObject()).trim().toLowerCase();
        String password = ((String) in.readObject()).trim();
        String role = ((String) in.readObject()).trim().toLowerCase();

        boolean authenticated = userDAO.authenticate(username, password, role);

        out.writeBoolean(authenticated);
        out.flush();
        System.out.println(authenticated ? "Login successful: " + username : "Login failed: " + username);
    }

    private void handleAddStudent(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        //handle adding student to the database/studentdao
        Student student = (Student) in.readObject();
        boolean success = studentDAO.addStudent(student);
        out.writeBoolean(success);
        out.flush();
        System.out.println("Add student " + (success ? "successful" : "failed") + ": " + student.getStudentNumber());
    }

    private void handleAddCourse(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        //addcourse to the database/coursedao
        Course course = (Course) in.readObject();
        boolean success = courseDAO.addCourse(course);
        out.writeBoolean(success);
        out.flush();
        System.out.println("Add course " + (success ? "successful" : "failed") + ": " + course.getCourseCode());
    }

    private void handleGetCourses(ObjectOutputStream out) throws Exception {
        //get available course through the coursedao/database
        List<Course> courses = courseDAO.getAllCourses();
        out.writeObject(courses);
        out.flush();
        System.out.println("Sent " + courses.size() + " courses to client");
    }

    private void handleEnrolStudent(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        //student enroll to course
        String studentNumber = ((String) in.readObject()).trim().toLowerCase();
        String courseCode = ((String) in.readObject()).trim().toUpperCase();
        boolean success = enrollmentDAO.enrolStudent(studentNumber, courseCode);
        out.writeBoolean(success);
        out.flush();
        System.out.println("Enrollment " + (success ? "successful" : "failed") + ": " + studentNumber + " in " + courseCode);
    }

    private void handleGetStudentCourses(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        String studentNumber = ((String) in.readObject()).trim().toLowerCase();
        List<Course> courses = enrollmentDAO.getCoursesForStudent(studentNumber);
        out.writeObject(courses);
        out.flush();
        System.out.println("Sent " + courses.size() + " courses for student: " + studentNumber);
    }

    private void handleGetCoursesByStudent(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        try {
            String studentId = (String) in.readObject();
            System.out.println("SERVER>>> Getting courses for student: " + studentId);

            List<Course> courses = enrollmentDAO.getCoursesForStudent(studentId);
            out.writeObject(courses);
            out.flush();

            System.out.println("SERVER>>> Sent " + courses.size() + " courses for student " + studentId);
        } catch (Exception e) {
            System.out.println("SERVER>> Error getting courses by student: " + e.getMessage());
            e.printStackTrace();
            out.writeObject(new ArrayList<Course>());
            out.flush();
        }
    }

    private void handleGetStudentsByCourse(ObjectInputStream in, ObjectOutputStream out) {
    try {
        // Read the course code from the client
        String courseCode = (String) in.readObject();
        courseCode = courseCode.trim();
        System.out.println("SERVER>>> Getting students for course: " + courseCode);

        // Check if the course exists
        Course course = courseDAO.getCourseByCode(courseCode);
        if (course == null) {
            System.out.println("SERVER>>> Course not found: " + courseCode);
            out.writeObject(new ArrayList<Student>());
            out.flush();
            return;
        }

        // Get all students enrolled in that course
        List<Student> students = enrollmentDAO.getStudentsByCourse(courseCode);

        if (students.isEmpty()) {
            System.out.println("SERVER>>> No students found for course: " + courseCode);
        } else {
            System.out.println("SERVER>>> Found " + students.size() + " student(s):");
            for (Student s : students) {
                System.out.println("   " + s.getStudentNumber() + " - " + s.getFirstName() + " " + s.getLastName());
            }
        }

        // Send the list of students back to the client
        out.writeObject(students);
        out.flush();
        System.out.println("SERVER>>> Sent " + students.size() + " student(s) for course: " + courseCode);

    } catch (Exception e) {
        System.out.println("SERVER>>> Error getting students by course: " + e.getMessage());
        e.printStackTrace();

        try {
            out.writeObject(new ArrayList<Student>());
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

}