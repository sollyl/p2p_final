package za.ac.cput.forreal;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import za.ac.cput.forreal.abstractBase.base;

public class Step3Controller extends base implements Initializable {

    @FXML
    private ScrollPane scroller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        verticalScroll(scroller);    
    }
}
