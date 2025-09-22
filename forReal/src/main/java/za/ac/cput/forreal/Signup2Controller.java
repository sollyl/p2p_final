package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import za.ac.cput.forreal.abstractBase.base;

public class Signup2Controller extends base implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void step1(MouseEvent event) throws IOException {
        loadScene("step1.fxml");
    }
    
}
