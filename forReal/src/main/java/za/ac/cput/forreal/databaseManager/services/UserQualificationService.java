package za.ac.cput.forreal.databaseManager.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import za.ac.cput.forreal.databaseManager.DBConnection;

public class UserQualificationService {
     // Check if student qualifies as tutor: must be 2nd year+ and have a mark >= 70
    public static boolean qualifiesAsTutor(String studentNumber) {
        // First check year of study
        String yearSql = "SELECT year_of_study FROM students WHERE student_number = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement yearStmt = con.prepareStatement(yearSql)) {

            yearStmt.setString(1, studentNumber);
            ResultSet rsYear = yearStmt.executeQuery();

            if (rsYear.next()) {
                int yearOfStudy = rsYear.getInt("year_of_study");

                // Must be 2nd year or above
                if (yearOfStudy < 2) {
                    return false;
                }
            } else {
                return false; // student not found
            }

        } catch (SQLException e) {
            System.err.println("Error checking year of study: " + e.getMessage());
            return false;
        }

        // Now check marks
        String markSql = "SELECT COUNT(*) FROM marks WHERE student_number = ? AND mark >= 70";
        try (Connection con = DBConnection.connect();
             PreparedStatement markStmt = con.prepareStatement(markSql)) {

            markStmt.setString(1, studentNumber);
            ResultSet rsMarks = markStmt.executeQuery();

            if (rsMarks.next()) {
                int qualifiedModules = rsMarks.getInt(1);
                return qualifiedModules >= 1; // at least one module ≥70
            }

        } catch (SQLException e) {
            System.err.println("Error checking tutor qualification marks: " + e.getMessage());
        }

        return false;
    }
    
    public static String getUserType(String studentNumber) {
        if (qualifiesAsTutor(studentNumber)) {
            return "TUTOR_AND_STUDENT";
        } else {
            return "STUDENT_ONLY";
        }
    }
    
    public static void updateUserRole(String studentNumber, String userType) {
        String sql = "UPDATE users SET is_tutor = ? WHERE student_number = ?";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            boolean isTutor = "TUTOR".equals(userType) || "TUTOR_AND_STUDENT".equals(userType);
            pstmt.setBoolean(1, isTutor);
            pstmt.setString(2, studentNumber);
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating user role: " + e.getMessage());
        }
    }

    public static void updateLoggedInAs(String studentNumber, String loggedInAs) {
        String sql = "UPDATE users SET logged_in_as = ? WHERE student_number = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, loggedInAs);
            pstmt.setString(2, studentNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating logged_in_as: " + e.getMessage());
        }
    }
    
    public static List<String> getEligibleTutorSubjects(String studentNumber) {
        List<String> subjects = new ArrayList<>();
        String sql = "SELECT m.module_name " +
                     "FROM marks mk " +
                     "JOIN modules m ON mk.module_code = m.module_code " +
                     "WHERE mk.student_number = ? " +
                     "AND mk.mark >= 70";

        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.add(rs.getString("module_name"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching eligible tutor subjects: " + e.getMessage());
        }

        return subjects;
    }
    
    // NEW METHOD: Get user role from database
    public static String getUserRoleFromDatabase(String studentNumber) {
        String sql = "SELECT account_type FROM students WHERE student_number = ?";
        
        try (Connection con = DBConnection.connect(); 
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("account_type");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user role from DB: " + e.getMessage());
        }
        return null;
    }
}