package za.ac.cput.studentenrollmentapp.connection;

import java.sql.*;

public class DBConnection {
    public static Connection derbyConnection() throws SQLException {
        String DATABASE_URL = "jdbc:derby://localhost:1527/StudentEnrolmentDB;create=true";
        String username = "administrator";
        String password = "admin";
        Connection connection = DriverManager.getConnection(DATABASE_URL, username, password);
        return connection;
    }
}