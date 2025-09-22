package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import za.ac.cput.forreal.abstractBase.base;

public class Step1Controller extends base implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void step3(MouseEvent event) throws IOException {
        loadScene("step3.fxml");
    }

    @FXML
    private void step2(MouseEvent event) throws IOException {
        loadScene("step2.fxml");
    }
    
}
