package za.ac.cput.forreal.databaseManager.services;

import java.sql.*;
import za.ac.cput.forreal.databaseManager.DBConnection;

public class UserQualificationService {
    public static boolean qualifiesAsTutor(String studentNumber) {
        String sql = "SELECT COUNT(*) FROM marks WHERE student_number = ? AND mark >= 70";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int qualifiedModules = rs.getInt(1);
                // Require at least 3 modules with 70% or higher to qualify as tutor
                return qualifiedModules >= 3;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking tutor qualification: " + e.getMessage());
        }
        return false;
    }
    
    public static String getUserType(String studentNumber) {
        if (qualifiesAsTutor(studentNumber)) {
            return "TUTOR_AND_USER";
        } else {
            return "USER_ONLY";
        }
    }
    
    public static void updateUserRole(String studentNumber, String userType) {
        String sql = "UPDATE users SET is_tutor = ? WHERE student_number = ?";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            boolean isTutor = "TUTOR".equals(userType) || "TUTOR_AND_USER".equals(userType);
            pstmt.setBoolean(1, isTutor);
            pstmt.setString(2, studentNumber);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating user role: " + e.getMessage());
        }
    }
}
