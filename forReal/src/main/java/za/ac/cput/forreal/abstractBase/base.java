package za.ac.cput.forreal.abstractBase;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import static javafx.scene.input.KeyCode.*;
import javafx.stage.Stage;
import za.ac.cput.forreal.App;

public abstract class base {

    protected void loadScene(String fxmlFile) throws IOException {
        Stage stage = App.getPrimaryStage();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));

        // Bind root to stage size
        if (root instanceof javafx.scene.layout.Region region) {
            region.prefWidthProperty().bind(stage.widthProperty());
            region.prefHeightProperty().bind(stage.heightProperty());
        }

        if (stage.getScene() != null) {
            stage.getScene().setRoot(root);
        } else {
            stage.setScene(new javafx.scene.Scene(root));
        }

        // Hide ESC hint for subsequent scenes
        App.hideFullScreenHint();

        javafx.application.Platform.runLater(() -> root.requestFocus());
    }


    /** Configure vertical scrolling for ScrollPane */
    protected void verticalScroll(ScrollPane scroller) {
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.setFitToWidth(true);
    }

    /** Get the primary stage */
    protected Stage getPrimaryStage() {
        return App.getPrimaryStage();
    }

    /** Exit the application */
    protected void exitApp() {
        Platform.exit();
    }
    
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    protected void setupPhoneNumberField(TextField phoneField) {
        // Set initial value
        phoneField.setText("+27 ");
        phoneField.positionCaret(phoneField.getText().length());

// TextFormatter to enforce +27 and 9 digits
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Always start with +27
            if (!newText.startsWith("+27 ")) {
                return null;
            }

            // Only allow up to 9 digits after +27
            String afterPrefix = newText.substring(4);
            if (!afterPrefix.matches("\\d{0,9}")) {
                return null;
            }

            return change;
        });

        phoneField.setTextFormatter(formatter);

        // Safety listener to auto-reset if user somehow removes +27
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.startsWith("+27 ")) {
                phoneField.setText("+27 ");
                phoneField.positionCaret(phoneField.getText().length());
            }
        });

        // Prevent backspace/delete from removing +27
        phoneField.setOnKeyPressed(event -> {
            int caretPos = phoneField.getCaretPosition();
            switch (event.getCode()) {
                case BACK_SPACE:
                case DELETE:
                    if (caretPos <= 3) {
                        event.consume(); // Block key
                    }
                    break;
                default:
                    break;
            }
        });
    }
    
    protected void tip(TextField txt, String message) {
        txt.setTooltip(new Tooltip(message));
    }
}
