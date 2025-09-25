package za.ac.cput.forreal.databaseManager;

import java.sql.*;

public class UserManager {

    // Check if student exists and is registered
    public static boolean isStudentRegistered(String studentNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE student_number = ? AND registration_complete = TRUE";
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking student registration: " + e.getMessage());
        }
        return false;
    }

    public static boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    public static String getStudentNumberByUsername(String username) {
        String sql = "SELECT student_number FROM users WHERE username = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("student_number");
            }
        } catch (SQLException e) {
            System.err.println("Error getting student number: " + e.getMessage());
        }
        return null;
    }

    public static boolean handleLogin(String usernameOrEmail, String password) {
        String username = usernameOrEmail;

        // If input is email, find corresponding username using student_number join
        if (usernameOrEmail.contains("@")) {
            username = getUsernameByEmail(usernameOrEmail);
            if (username == null) {
                // Email not yet registered in the system
                return false;
            }
        }

        // Check user credentials in the users table
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(
                "SELECT password, registration_complete FROM users WHERE username = ?")) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean complete = rs.getBoolean("registration_complete");
                String storedPassword = rs.getString("password");

                // Account exists but registration incomplete
                if (!complete) {
                    return false;
                }

                // Password check (unhashed for now)
                if (storedPassword != null && storedPassword.equals(password)) {
                    return true; // Login allowed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // User not found or wrong password
    }

    public static String getUsernameByEmail(String email) {
        String sql = "SELECT u.username FROM users u "
                + "JOIN students s ON u.student_number = s.student_number "
                + "WHERE s.email = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
