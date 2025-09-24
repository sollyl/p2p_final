package za.ac.cput.forreal;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class SimpleEmailSender {

    private static final String FROM_EMAIL = "peertopeertutoringapp@gmail.com";
    private static final String APP_PASSWORD = "api here";
    private static final String APP_NAME = "Peer to Peer Tutoring";

    public static boolean sendOTPEmail(String toEmail, String otpCode) {
        String subject = "Your OTP Code for " + APP_NAME;
        String bodyText = "Your verification code is: " + otpCode + 
                         "\n\nThis code will expire in 10 minutes." +
                         "\n\nIf you didn't request this code, please ignore this email.";
        
        return sendEmail(toEmail, subject, bodyText);
    }

    private static boolean sendEmail(String toEmail, String subject, String bodyText) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, APP_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(bodyText);

            Transport.send(message);
            System.out.println("✓ OTP email sent successfully to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send OTP email: " + e.getMessage());
            return false;
        }
    }
    
    // For testing/debugging - print OTP to console
    public static void printOTPToConsole(String email, String otpCode) {
        System.out.println("=== DEVELOPMENT MODE ===");
        System.out.println("Email: " + email);
        System.out.println("OTP Code: " + otpCode);
        System.out.println("========================");
    }
}