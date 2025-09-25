package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;
import za.ac.cput.forreal.databaseManager.UserManager;

public class Signup2Controller extends base implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField pass;
    @FXML
    private PasswordField com_pass;
    @FXML
    private AnchorPane sign2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private boolean updateUserCredentials(String studentNumber, String username, String password) {
        String sql = "UPDATE users SET username = ?, password = ? WHERE student_number = ?";

        try (var con = DBConnection.connect();
             var pstmt = con.prepareStatement(sql)) {

            String hashedPassword = Integer.toString(password.hashCode()); // replace with proper hash

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, studentNumber);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error updating user credentials: " + e.getMessage());
            return false;
        }
    }
    
    private void handleCompleteStep2(MouseEvent event) {
        String user = username.getText().trim();
        String password = pass.getText().trim();
        String confirm = com_pass.getText().trim();

        if (user.isEmpty() || password.isEmpty()) {
            showAlert(sign2, "Error", "Please fill in all fields.");
            return;
        }
        
        if (password.length() < 6) {
            showAlert(sign2, "Error", "Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirm)) {
            showAlert(sign2, "Error", "Passwords does not match.");
            return;
        }

        if (UserManager.isUsernameTaken(user)) {
            showAlert(sign2, "Error", "Username is already taken. Please choose another.");
            return;
        }
        String studentNumber = getCurrentStudentNumber();
        if (studentNumber == null) {
            showAlert(sign2, "Error", "Student number missing. Please restart signup.");
            return;
        }

        boolean updated = updateUserCredentials(studentNumber, user, password);

        if (updated) {
            setCurrentUsername(user);
            showAlertGreen(sign2, "Success", "Username and password saved! Continue to next step.");

            // Navigate to next page if needed
            try {
                loadScene("step1.fxml"); // example next step
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(sign2, "Error", "Failed to navigate to next step.");
            }

        } else {
            showAlert(sign2, "Error", "Failed to save credentials. Please try again.");
        }
    }

    @FXML
    private void step1(MouseEvent event) throws IOException {
        handleCompleteStep2(event);
    }
    
}
