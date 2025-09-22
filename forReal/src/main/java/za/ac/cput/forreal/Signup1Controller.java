package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import za.ac.cput.forreal.abstractBase.base;

public class Signup1Controller extends base implements Initializable {

    @FXML
    private TextField phone_num;
    @FXML
    private TextField f_name;
    @FXML
    private TextField stud_number;
    @FXML
    private TextField stud_email;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setupPhoneNumberField(phone_num);
        tip(phone_num, "Phone mumber must contain 9 digits");
    }

    @FXML
    private void signup2(MouseEvent event) throws IOException {
        loadScene("signup2.fxml");
    }

    @FXML
    private void login(MouseEvent event) throws IOException {
        loadScene("login.fxml");
    }

}
