package za.ac.cput.forreal;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import za.ac.cput.forreal.databaseManager.DBInitializer;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        Parent root = FXMLLoader.load(getClass().getResource("step2.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Peer to Peer Tutoring");

        // Add icon
        Image icon = new Image(getClass().getResourceAsStream("/Frame 6 (2).png"));
        primaryStage.getIcons().add(icon);

        primaryStage.setResizable(true);
        primaryStage.setFullScreen(true); // start fullscreen

        // Show ESC hint only on first scene
        primaryStage.setFullScreenExitHint("Press ESC to exit fullscreen");

        // Listener for ESC exit 
        primaryStage.fullScreenProperty().addListener((obs, wasFull, isFull) -> {
            if (!isFull) {
                Platform.runLater(() -> {
                    // Ensure fullscreen flag is off 
                    primaryStage.setFullScreen(false);
                    // Maximize window up to taskbar (does not cover taskbar) 
                    primaryStage.setMaximized(true);
                    primaryStage.setResizable(false);
                });
            }
        });

        primaryStage.show();

        Platform.runLater(() -> scene.getRoot().requestFocus());
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void hideFullScreenHint() {
        if (primaryStage != null) {
            primaryStage.setFullScreenExitHint(""); // disables ESC hint
        }
    }

    public static void main(String[] args) {
        //DBInitializer.initializeDB();
        launch();
    }
}
