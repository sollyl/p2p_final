package za.ac.cput.forreal;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;
import za.ac.cput.forreal.databaseManager.services.UserQualificationService;

public class Step3Controller extends base implements Initializable {

    private String studentNumber;
    private String selectCourse;

    @FXML
    private ScrollPane scroller;
    @FXML
    private Label header;
    @FXML
    private AnchorPane main;
    @FXML
    private Label head2;
    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        verticalScroll(scroller);
        textVibes();
    }

   private void textVibes() {
    String studentNumber = getCurrentStudentNumber();
    
    // This now uses the fixed base.getCurrentUserRole() which checks both tables
    String selectedRole = getCurrentUserRole();
    
    // DEBUG
    System.out.println("DEBUG Step3 - Student Number: " + studentNumber);
    System.out.println("DEBUG Step3 - Role: " + selectedRole);

    // Check which modules have >= 70%
    List<String> tutorSubjects = getEligibleTutorSubjects(studentNumber);

    String finalRole;
    String alertMessage;

    if (!tutorSubjects.isEmpty() && "TUTOR".equals(selectedRole)) {
        finalRole = "TUTOR";
        header.setText("Congratulations!");
        head2.setText("You qualify as both Tutor and Studet!");
        alertMessage = "You can now offer your expertise in:\n\n" + String.join("\n", tutorSubjects);
    } else if ("TUTOR".equals(selectedRole)) {
        finalRole = "STUDENT";
        header.setText("Notice");
        head2.setText("You applied as Tutor, but you don't have any modules above 70% yet.");
        alertMessage = "You are eligible to be a User, but you don't have any modules above 70% to qualify as a Tutor.";
    } else {
        finalRole = "STUDENT";
        header.setText("Welcome!");
        head2.setText("You're registered as a Student");
        alertMessage = "You can access tutoring services from qualified tutors.";
    }

    // Update role in DB
    UserQualificationService.updateUserRole(studentNumber, finalRole);
    UserQualificationService.updateLoggedInAs(studentNumber, finalRole);

    markAsRegistered(studentNumber);

    String finalAlertMessage = alertMessage;
    Platform.runLater(() -> showAlertGreen(main, "Congrats!", finalAlertMessage));
}
    
    
    private void markAsRegistered(String studentNumber) {
        String sql = "UPDATE users SET registration_complete = TRUE WHERE student_number = ?";
        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            pstmt.executeUpdate();
            System.out.println("DEBUG - Marked student as registered: " + studentNumber);
        } catch (SQLException e) {
            System.err.println("Error updating registration status: " + e.getMessage());
        }
    }

    @FXML
    private void goToHome(MouseEvent event) {
        try {
            loadScene("dashboard.fxml");
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
        }
    }

    // FIX: Correct the SQL query - remove the "* " 
    public static List<String> getEligibleTutorSubjects(String studentNumber) {
        List<String> subjects = new ArrayList<>();
        String sql = "SELECT m.module_name "
                + "FROM marks mk "
                + "JOIN modules m ON mk.module_code = m.module_code "
                + "WHERE mk.student_number = ? "
                + "AND mk.mark >= 70";

        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // FIX: Remove the "* " - it was causing SQL errors
                subjects.add(rs.getString("module_name"));
            }

            System.out.println("DEBUG - Found " + subjects.size() + " tutor subjects for student: " + studentNumber);

        } catch (SQLException e) {
            System.err.println("Error fetching eligible tutor subjects: " + e.getMessage());
        }

        return subjects;
    }
}