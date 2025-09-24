package za.ac.cput.forreal.databaseManager.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import za.ac.cput.forreal.databaseManager.DBConnection;

public class MarkValidationservice {
    public static boolean validateMarksAgainstDatabase(String studentNumber, String moduleCode, int enteredMark) {
        String sql = "SELECT mark FROM marks WHERE student_number = ? AND module_code = ?";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            pstmt.setString(2, moduleCode);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int databaseMark = rs.getInt("mark");
                return databaseMark == enteredMark;
            }
            
        } catch (SQLException e) {
            System.err.println("Error validating marks: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean validateAllMarks(String studentNumber, Map<String, Integer> enteredMarks) {
        for (Map.Entry<String, Integer> entry : enteredMarks.entrySet()) {
            if (!validateMarksAgainstDatabase(studentNumber, entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
