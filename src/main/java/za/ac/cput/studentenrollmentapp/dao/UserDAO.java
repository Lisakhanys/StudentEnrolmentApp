package za.ac.cput.studentenrollmentapp.dao;

import za.ac.cput.studentenrollmentapp.connection.DBConnection;
import java.sql.*;

public class UserDAO {

    public boolean authenticate(String username, String password, String role) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.toLowerCase());
            ps.setString(2, password);
            ps.setString(3, role.toLowerCase());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("UserDAO authenticate error: " + e.getMessage());
            return false;
        }
    }
}
