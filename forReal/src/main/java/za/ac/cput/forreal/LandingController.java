package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import za.ac.cput.forreal.abstractBase.base;

public class LandingController extends base implements Initializable {

    @FXML
    private ScrollPane scroller;
    @FXML
    private VBox featuresSection;
    @FXML
    private VBox howItWorksSection;
    @FXML
    private VBox aboutUs;
    @FXML
    private HBox bottom;
    @FXML
    private HBox howItWorksNav;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        verticalScroll(scroller);
    }

    @FXML
    private void login(MouseEvent event) throws IOException {
        loadScene("login.fxml");
    }

    private void animateScrollTo(double targetY) {
        final double currentY = scroller.getVvalue() * scroller.getContent().getBoundsInLocal().getHeight();
        final double diff = targetY - currentY;

        Timeline timeline = new Timeline();
        final int frames = 20; // Number of animation frames
        final Duration duration = Duration.millis(400); // Animation duration

        for (int i = 0; i <= frames; i++) {
            final double progress = (double) i / frames;
            KeyFrame keyFrame = new KeyFrame(duration.multiply(progress), event -> {
                double newY = currentY + (diff * progress);
                double vvalue = newY / scroller.getContent().getBoundsInLocal().getHeight();
                scroller.setVvalue(vvalue);
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }

    @FXML
    private void scrollToHome(MouseEvent event) {
        animateScrollTo(0);
    }

    @FXML
    private void scrollToFeatures(MouseEvent event) {
        double targetY = featuresSection.getLayoutY();
        animateScrollTo(targetY);
    }

    @FXML
    private void scrollToHowItWorks(MouseEvent event) {
        double targetY = aboutUs.getLayoutY();
        animateScrollTo(targetY);
    }

    @FXML
    private void scrollToAboutSection(MouseEvent event) {
        double targetY = bottom.getLayoutY();
        animateScrollTo(targetY);
    }

}
