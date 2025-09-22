package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import za.ac.cput.forreal.abstractBase.base;

public class Step2Controller extends base implements Initializable {

    @FXML
    private ScrollPane scroller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        verticalScroll(scroller);
    }    

    @FXML
    private void step1(MouseEvent event) throws IOException {
        loadScene("step1.fxml");
    }

    @FXML
    private void step3(MouseEvent event) throws IOException {
        loadScene("step3.fxml");
    }

    @FXML
    private void deleteModule(MouseEvent event) {
    }

    @FXML
    private void addModules(MouseEvent event) {
    }
    
}
