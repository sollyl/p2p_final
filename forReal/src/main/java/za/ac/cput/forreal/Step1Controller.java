package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;

public class Step1Controller extends base implements Initializable {
    
    private int studentYear;
    private String studentCourse;
    @FXML
    private ComboBox<String> stepCombo;
    @FXML
    private ComboBox<String> stepCombo11;
    @FXML
    private ComboBox<String> stepCombo1;
    @FXML
    private AnchorPane main;
    @FXML
    private Label clickable;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        String studentNumber = getCurrentStudentNumber();
        
        // Get student details from database
        getStudentDetails(studentNumber);
        
        // Update UI with student's actual course and year
        updateStudentInfoUI();
        
        // Update skip button visibility based on year
        if (studentYear == 1) {
            clickable.setVisible(true);
            clickable.setDisable(false);
        } else {
            clickable.setVisible(false);
            clickable.setDisable(true);
        }
        
        // Only show role selection (Tutor/User)
        stepCombo.getItems().addAll("Tutor", "User");
        stepCombo.setValue("Select");
        
        // Make course and year read-only with bold teal style instead of disabled
        styleReadOnlyCombos();
    }    
    
    private void getStudentDetails(String studentNumber) {
        String sql = "SELECT sub_name, year_of_study FROM students WHERE student_number = ?";
        
        try (var con = DBConnection.connect();
             var pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            var rs = pstmt.executeQuery();
            
            if (rs.next()) {
                studentCourse = rs.getString("sub_name");
                studentYear = rs.getInt("year_of_study");
            }
            
        } catch (Exception e) {
            System.err.println("Error getting student details: " + e.getMessage());
        }
    }
    
    private void updateStudentInfoUI() {
        stepCombo1.setValue(studentCourse);
        stepCombo11.setValue(String.valueOf(studentYear));
    }
    
    private void styleReadOnlyCombos() {
        // Apply bold teal style to make them look read-only but not disabled
        String readOnlyStyle = "-fx-font-weight: bold; -fx-background-color: #E6EBF0;";
        
        stepCombo1.setStyle(readOnlyStyle);
        stepCombo11.setStyle(readOnlyStyle);
        
        // Make them non-editable and non-focusable
        stepCombo1.setEditable(false);
        stepCombo11.setEditable(false);
        stepCombo1.setMouseTransparent(true);
        stepCombo11.setMouseTransparent(true);
        stepCombo1.setFocusTraversable(false);
        stepCombo11.setFocusTraversable(false);
    }

    @FXML
    private void step3(MouseEvent event) throws IOException {
        loadScene("step3.fxml");
    }

    @FXML
    private void step2(MouseEvent event) throws IOException {
        String role = stepCombo.getValue();
        
        if ("Select".equals(role)) {
            showAlert(main, "Error", "Please select your role");
            return;
        }
        loadScene("step2.fxml");
    }
    
}
