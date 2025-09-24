package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;
import za.ac.cput.forreal.databaseManager.OTPManager;
import za.ac.cput.forreal.databaseManager.UserManager;

public class Signup1Controller extends base implements Initializable {

    @FXML
    private TextField phone_num;
    @FXML
    private TextField f_name;
    @FXML
    private TextField stud_number;
    @FXML
    private TextField stud_email;
    
    private String tempStudentNumber, tempEmail;
    @FXML
    private Label sent_text;
    @FXML
    private TextField auth1;
    @FXML
    private TextField auth2;
    @FXML
    private TextField auth3;
    @FXML
    private TextField auth4;
    @FXML
    private Pane auth_pane;
    @FXML
    private AnchorPane main_vibe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setupPhoneNumberField(phone_num);
        tip(phone_num, "Phone mumber must contain 9 digits");
        
        restrictToOneDigit(auth1);
        restrictToOneDigit(auth2);
        restrictToOneDigit(auth3);
        restrictToOneDigit(auth4);
        autoFocus(auth1, auth2, null);
        autoFocus(auth2, auth3, auth1);
        autoFocus(auth3, auth4, auth2);
        autoFocus(auth4, null, auth3);
        auth_pane.setVisible(false);
    }
    
    private boolean verifyStudent() {
        String name = f_name.getText().trim();
        String num = stud_number.getText().trim();
        String email = stud_email.getText().trim();
        String phone = phone_num.getText().trim();
        
        if (name.isEmpty() || num.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(main_vibe,"Error", "Please fill in all required fields");
            return false;
        }
        
        // This is basically to check if student exists in the database
        String studentEmail = OTPManager.getStudentEmail(num);
        if (studentEmail == null || !studentEmail.equalsIgnoreCase(email)) {
            showAlert(main_vibe, "Error", "Student number or email don't match our records");
            return false;
        }

        // Check if already registere
        if (UserManager.isStudentRegistered(num)) {
            showAlert(main_vibe, "Error", "Student number already registered. Please login.");
            return false;
        }
        String phoneDigits = phone.substring(4);
        if (!phoneDigits.matches("\\d{9}")) {
            showAlert(main_vibe, "Error", "Please enter a valid 9-digit phone number after +27");
            return false;
        }
        
        if (phoneDigits.charAt(0) == '0') {
            showAlert(main_vibe, "Error", "Phone number cannot start with 0 after +27");
            return false;
        }

        //This is the email intergration guys
        String otpCode = OTPManager.generateOTP(email);

        if (otpCode != null) {
            tempStudentNumber = num;
            tempEmail = email;
            showAlertGreen(main_vibe,"Success", "OTP sent to your email!");
            return true;
        } else {
            showAlert(main_vibe,"Error", "Failed to generate OTP. Please try again.");
            return false;
        }
        
    }
    
    private boolean createTemporaryUser(String phone, String fullName) {
        // This creates a user but marks them as not completed registration
        String sql = "INSERT INTO users (student_number, phone, full_name, registration_complete) " +
                   "VALUES (?, ?, ?, FALSE)";
        
        try (var con = DBConnection.connect();
             var pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, tempStudentNumber);
            pstmt.setString(2, phone);
            pstmt.setString(3, fullName);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.err.println("Error creating temporary user: " + e.getMessage());
            return false;
        }
    }
    
    private void completeRegistration(MouseEvent event) {
        String phone = phone_num.getText().trim();
        String fullName = f_name.getText().trim();

        // Create user account with temporary flag (not active until steps complete)
        boolean success = createTemporaryUser(phone, fullName);
        
        if (success) {
            // Store user info for the steps
            setCurrentUser(tempStudentNumber);
            
            // Navigate to step 1
            try {
                loadScene("signup2.fxml");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(main_vibe, "Error", "Failed to navigate to next step");
            }
        } else {
            showAlert(main_vibe, "Error", "Registration failed. Please try again.");
        }
    }

    @FXML
    private void login(MouseEvent event) throws IOException {
        loadScene("login.fxml");
    }

    @FXML
    private void handleAuth(MouseEvent event) {
        if (verifyStudent()) {
            auth_pane.setVisible(true);
            sent_text.setText("The code was sent to " + stud_email.getText());
        }
    }

    @FXML
    private void goToPart2(MouseEvent event) {
        String enteredOtp = auth1.getText() + auth2.getText() + auth3.getText() + auth4.getText();
        
        if (enteredOtp.length() != 4) {
            showAlert(main_vibe, "Error", "Please enter a valid 4-digit OTP");
            return;
        }
        
        // Validate OTP against database
        String studentEmail = OTPManager.getStudentEmail(tempStudentNumber);
        if (studentEmail != null && OTPManager.validateOTP(studentEmail , enteredOtp)) {
            completeRegistration(event);
        } else {
            showAlert(main_vibe, "Error", "Invalid OTP. Please try again.");
        }
    }

}