package za.ac.cput.studentenrollmentapp.connection;

import java.sql.*;

public class CreateTable {

    public static void createAllTables() {
        System.out.println("=== CREATING DATABASE TABLES ===");

        String createStudentsTable = "CREATE TABLE students (" +
                "studentNumber VARCHAR(10) NOT NULL PRIMARY KEY, " +
                "firstName VARCHAR(50) NOT NULL, " +
                "lastName VARCHAR(50) NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "email VARCHAR(50) NOT NULL, " +
                "role VARCHAR(10) NOT NULL)";

        String createCourseTable = "CREATE TABLE course (" +
                "course_code VARCHAR(10) PRIMARY KEY, " +
                "course_name VARCHAR(100) NOT NULL)";

        String createEnrolmentTable = "CREATE TABLE enrolment (" +
                "studentNumber VARCHAR(10) NOT NULL, " +
                "course_code VARCHAR(10) NOT NULL, " +
                "PRIMARY KEY (studentNumber, course_code), " +
                "FOREIGN KEY (studentNumber) REFERENCES students(studentNumber), " +
                "FOREIGN KEY (course_code) REFERENCES course(course_code))";

        String createUsersTable = "CREATE TABLE users (" +
                "username VARCHAR(50) PRIMARY KEY, " +
                "password VARCHAR(50) NOT NULL, " +
                "role VARCHAR(10) NOT NULL)";

        try (Connection conn = DBConnection.derbyConnection()) {

            System.out.println("Connected to Derby Network Server");

            // Create students table
            try (PreparedStatement pstmt = conn.prepareStatement(createStudentsTable)) {
                pstmt.execute();
                System.out.println("Students table created successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("X0Y32")) {
                    System.out.println("Students table already exists");
                } else throw e;
            }

            // Create course table
            try (PreparedStatement pstmt = conn.prepareStatement(createCourseTable)) {
                pstmt.execute();
                System.out.println("Course table created successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("X0Y32")) {
                    System.out.println("Course table already exists");
                } else throw e;
            }

            // Create enrolment table
            try (PreparedStatement pstmt = conn.prepareStatement(createEnrolmentTable)) {
                pstmt.execute();
                System.out.println("Enrolment table created successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("X0Y32")) {
                    System.out.println("Enrolment table already exists");
                } else throw e;
            }

            // Create users table
            try (PreparedStatement pstmt = conn.prepareStatement(createUsersTable)) {
                pstmt.execute();
                System.out.println("Users table created successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("X0Y32")) {
                    System.out.println("Users table already exists");
                } else throw e;
            }

            System.out.println("ALL TABLES CREATED SUCCESSFULLY\n");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertSampleData() {
        System.out.println("=== INSERTING SAMPLE DATA ===");

        String insertUser = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        String insertStudent = "INSERT INTO students (studentNumber, firstName, lastName, password, email, role) VALUES (?, ?, ?, ?, ?, ?)";
        String insertCourse = "INSERT INTO course (course_code, course_name) VALUES (?, ?)";

        try (Connection conn = DBConnection.derbyConnection()) {

            // Insert users
            try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                ps.setString(1, "admin");
                ps.setString(2, "admin123");
                ps.setString(3, "admin");
                ps.execute();

                ps.setString(1, "s001");
                ps.setString(2, "lisa123");
                ps.setString(3, "student");
                ps.execute();

                ps.setString(1, "s002");
                ps.setString(2, "avu123");
                ps.setString(3, "student");
                ps.execute();

                ps.setString(1, "s003");
                ps.setString(2, "sam@gmail.com");
                ps.setString(3, "student");
                ps.execute();

                System.out.println("Users inserted successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) {
                    System.out.println("Users already exist");
                } else throw e;
            }

            // Insert students
            try (PreparedStatement ps = conn.prepareStatement(insertStudent)) {
                ps.setString(1, "s001");
                ps.setString(2, "Lisakhanya");
                ps.setString(3, "Tshokolo");
                ps.setString(4, "lisa123");
                ps.setString(5, "lisa@mycput.com");
                ps.setString(6, "student");
                ps.execute();

                ps.setString(1, "s002");
                ps.setString(2, "Avuyile");
                ps.setString(3, "Sitoyi");
                ps.setString(4, "avu123");
                ps.setString(5, "avu@icloud.com");
                ps.setString(6, "student");
                ps.execute();

                ps.setString(1, "s003");
                ps.setString(2, "Smukelo");
                ps.setString(3, "Ndela");
                ps.setString(4, "sam123");
                ps.setString(5, "sam@gmail.com");
                ps.setString(6, "student");
                ps.execute();

                System.out.println("Students inserted successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) {
                    System.out.println("Students already exist");
                } else throw e;
            }

            // Insert courses
            try (PreparedStatement ps = conn.prepareStatement(insertCourse)) {
                ps.setString(1, "CNF262S");
                ps.setString(2, "Communication Network Fundamentals");
                ps.execute();

                ps.setString(1, "ADP262S");
                ps.setString(2, "Application Development Practice");
                ps.execute();

                ps.setString(1, "ICE262S");
                ps.setString(2, "Elective 2");
                ps.execute();

                ps.setString(1, "PRT262S");
                ps.setString(2, "Project 2");
                ps.execute();

                System.out.println("Courses inserted successfully");
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) {
                    System.out.println("Courses already exist");
                } else throw e;
            }

            System.out.println("SAMPLE DATA INSERTED SUCCESSFULLY\n");

        } catch (SQLException e) {
            System.out.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createAllTables();
        insertSampleData();
    }
}
