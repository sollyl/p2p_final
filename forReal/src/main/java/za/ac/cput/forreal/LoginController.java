package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;
import static za.ac.cput.forreal.databaseManager.UserManager.getUsernameByEmail;

public class LoginController extends base implements Initializable {

    @FXML
    private AnchorPane main_vibe;
    @FXML
    private TextField use;
    @FXML
    private PasswordField pass;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void login(MouseEvent event) {
        String input = use.getText().trim();
        String password = pass.getText();

        if (input.isEmpty() || password.isEmpty()) {
            showAlert(main_vibe, "Error", "Please enter username/email and password");
            return;
        }

        String username = input.contains("@") ? getUsernameByEmail(input) : input;

        if (username == null) {
            // Email not registered
            showAlert(main_vibe, "Error", "Email not yet registered. Please signup.");
            return;
        }

        // Check credentials and registration status
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(
                 "SELECT password, registration_complete, student_number FROM users WHERE username = ?")) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean complete = rs.getBoolean("registration_complete");
                String storedPassword = rs.getString("password");
                String studentNumber = rs.getString("student_number");

                if (!complete) {
                    showAlert(main_vibe, "Error", "Account incomplete. Please complete signup.");
                    return;
                }

                if (storedPassword != null && storedPassword.equals(password)) {
                    // Login successful
                    setCurrentUsername(username);
                    setCurrentUser(studentNumber);
                    showAlertGreen(main_vibe, "Success", "Login successful!");

                    // TODO: navigate to homepage
                    // loadScene("homepage.fxml");

                } else {
                    showAlert(main_vibe, "Error", "Username or password is incorrect.");
                }
            } else {
                showAlert(main_vibe, "Error", "Username or password is incorrect.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(main_vibe, "Error", "An error occurred while logging in.");
        }
    }

    @FXML
    private void signup1(MouseEvent event) throws IOException {
        loadScene("signup1.fxml");
    }

    @FXML
    private void mainPage(MouseEvent event) {
        login(event);
    }

}
