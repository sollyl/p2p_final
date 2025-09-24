package za.ac.cput.forreal.databaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;
import za.ac.cput.forreal.SimpleEmailSender;

public class OTPManager {
    
    // Generate random 4-digit OTP
    private static String generateRandomOTP() {
        Random random = new Random();
        int num = 1000 + random.nextInt(9000);
        return String.valueOf(num);
    }
    
    // Mark OTP as used
    private static void markOTPAsUsed(int otpId) {
        String sql = "UPDATE otps SET used = TRUE WHERE id = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, otpId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking OTP as used: " + e.getMessage());
        }
    }
    
    private static void cleanupExpiredOTPs() {
        String sql = "DELETE FROM otps WHERE expires < CURRENT_TIMESTAMP OR used = TRUE";
        try (Connection con = DBConnection.connect();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error cleaning up OTPs: " + e.getMessage());
        }
    }
    // Generate and store OTP
    public static String generateOTP(String email) {
        // Clean up expired OTPs first
        cleanupExpiredOTPs();
        
        String otpCode = generateRandomOTP();
        
        String sql = "INSERT INTO otps (email, code, expires) VALUES (?, ?, ?)";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            LocalDateTime expires = LocalDateTime.now().plusMinutes(10);
            
            pstmt.setString(1, email);
            pstmt.setString(2, otpCode);
            pstmt.setTimestamp(3, Timestamp.valueOf(expires));
            
            pstmt.executeUpdate();
            
            // Send OTP via email - THIS IS THE KEY INTEGRATION
            boolean emailSent = SimpleEmailSender.sendOTPEmail(email, otpCode);
            
            if (emailSent) {
                System.out.println("✓ OTP sent successfully to: " + email);
                return otpCode;
            } else {
                // Fallback: print to console for development
                System.out.println("⚠ Email failed, using console fallback");
                SimpleEmailSender.printOTPToConsole(email, otpCode);
                return otpCode;
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating OTP: " + e.getMessage());
            return null;
        }
    }
    
    // Validate OTP
    public static boolean validateOTP(String email, String code) {
        cleanupExpiredOTPs();
        
        String sql = "SELECT id, expires, used FROM otps WHERE email = ? AND code = ? " +
                     "ORDER BY created DESC LIMIT 1";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, code);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Timestamp expires = rs.getTimestamp("expires");
                boolean used = rs.getBoolean("used");
                
                if (LocalDateTime.now().isAfter(expires.toLocalDateTime()) || used) {
                    return false;
                }
                
                markOTPAsUsed(rs.getInt("id"));
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error validating OTP: " + e.getMessage());
        }
        return false;
    }
    
    // Get student email by student number
    public static String getStudentEmail(String studentNumber) {
        String sql = "SELECT email FROM students WHERE student_number = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            System.err.println("Error getting student email: " + e.getMessage());
        }
        return null;
    }
    
    public static String generateAndSendOTP(String email) {
        cleanupExpiredOTPs();
        
        String otpCode = generateRandomOTP();
        
        String sql = "INSERT INTO otps (email, code, expires) VALUES (?, ?, ?)";
        
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            LocalDateTime expires = LocalDateTime.now().plusMinutes(10);
            
            pstmt.setString(1, email);
            pstmt.setString(2, otpCode);
            pstmt.setTimestamp(3, Timestamp.valueOf(expires));
            
            pstmt.executeUpdate();
            
            // Try to send email
            boolean emailSent = SimpleEmailSender.sendOTPEmail(email, otpCode);
            
            if (emailSent) {
                return "EMAIL_SENT";
            } else {
                // Fallback for development - print to console
                SimpleEmailSender.printOTPToConsole(email, otpCode);
                return "CONSOLE_FALLBACK";
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating OTP: " + e.getMessage());
            return "ERROR";
        }
    }

}