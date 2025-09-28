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
        String selectedRole = getCurrentUserRole(); // from Step1

        // Check which modules have >= 70%
        List<String> tutorSubjects = getEligibleTutorSubjects(studentNumber);

        String finalRole;
        String alertMessage;

        if (!tutorSubjects.isEmpty() && selectedRole.equals("TUTOR")) {
            finalRole = "TUTOR";
            header.setText("Congratulations!");
            head2.setText("You qualify as both Tutor and User!");
            alertMessage = "You can now offer your expertise in:\n\n" + tutorSubjects;
        } else if (selectedRole.equals("TUTOR")) {
            // They wanted to be a tutor but no module >= 70%
            finalRole = "USER";
            header.setText("Notice");
            head2.setText("You applied as Tutor, but you don't have any modules above 70% yet.");
            alertMessage = "You are eligible to be a User, but you don't have any modules above 70% to qualify as a Tutor.";
        } else { // They selected USER
            finalRole = "USER";
            header.setText("Welcome!");
            head2.setText("You're registered as a User");
            alertMessage = "You can access tutoring services from qualified tutors.";
        }

        // Update database
        UserQualificationService.updateUserRole(studentNumber, finalRole);
        UserQualificationService.updateLoggedInAs(studentNumber, finalRole);

        // Show alert
        String finalAlertMessage = alertMessage;
        Platform.runLater(() -> showAlertGreen(main, "Info", finalAlertMessage));
    }
    
     public static List<String> getEligibleTutorSubjects(String studentNumber) {
        List<String> subjects = new ArrayList<>();
        String sql = "SELECT m.module_name "
                + "FROM marks mk "
                + "JOIN modules m ON mk.module_code = m.module_code "
                + "WHERE mk.student_number = ? "
                + "AND mk.mark >= 70"; // only marks >= 70

        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.add(rs.getString("* " + "module_name") + "\n");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching eligible tutor subjects: " + e.getMessage());
        }

        return subjects;
    }

}
