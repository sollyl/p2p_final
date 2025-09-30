package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import za.ac.cput.forreal.abstractBase.base;

public class DashboardController extends base implements Initializable {

    @FXML
    private ImageView prof;
    @FXML
    private Label username;
    @FXML
    private Label account_type;
    @FXML
    private Label display_fullName;
    @FXML
    private HBox holds_modules;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        currentStudentNumber = getCurrentStudentNumber();
        username.setText(getCurrentUsername());
        Circle clip = new Circle(20, 20, 20); // centerX, centerY, radius
        prof.setClip(clip);
    }

    @FXML
    private void timetable(MouseEvent event) throws IOException {
        loadScene("timetable.fxml");
    }
    
}
