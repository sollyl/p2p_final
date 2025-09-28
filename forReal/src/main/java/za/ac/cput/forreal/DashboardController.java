package za.ac.cput.forreal;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class DashboardController implements Initializable {

    @FXML
    private ImageView prof;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Circle clip = new Circle(20, 20, 20); // centerX, centerY, radius
        prof.setClip(clip);
    } 
    
}
