package za.ac.cput.forreal.abstractBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import za.ac.cput.forreal.App;
import za.ac.cput.forreal.databaseManager.DBConnection;

public abstract class base {

    protected static String currentStudentNumber;
    protected static String currentUsername;
    protected static String currentUserRole;
    
    public static void setCurrentUser(String studentNumber) {
        currentStudentNumber = studentNumber;
    }

    public static String getCurrentStudentNumber() {
        return currentStudentNumber;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    public static String getCurrentUserRole() {
    // Always fetch from database to ensure we have the latest value
    if (currentStudentNumber == null || currentStudentNumber.trim().isEmpty()) {
        return "USER"; // default fallback
    }
    
    // Try users table first (logged_in_as)
    String sql = "SELECT logged_in_as FROM users WHERE student_number = ?";
    
    try (Connection con = DBConnection.connect();
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        
        pstmt.setString(1, currentStudentNumber);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String role = rs.getString("logged_in_as");
            if (role != null && !role.trim().isEmpty()) {
                return role;
            }
        }
        
    } catch (Exception e) {
        System.err.println("Error fetching user role from users table: " + e.getMessage());
    }
    
    // If not found in users table, try students table (account_type)
    sql = "SELECT account_type FROM students WHERE student_number = ?";
    
    try (Connection con = DBConnection.connect();
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        
        pstmt.setString(1, currentStudentNumber);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String role = rs.getString("account_type");
            return (role != null && !role.trim().isEmpty()) ? role : "USER";
        }
        
    } catch (Exception e) {
        System.err.println("Error fetching user role from students table: " + e.getMessage());
    }
    
    return "USER"; // default fallback
}

    public static boolean isUserLoggedIn() {
        return currentUsername != null && !currentUsername.trim().isEmpty();
    }
    
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

    /**
     * Configure vertical scrolling for ScrollPane
     */
    protected void verticalScroll(ScrollPane scroller) {
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.setFitToWidth(true);
    }

    /**
     * Get the primary stage
     */
    protected Stage getPrimaryStage() {
        return App.getPrimaryStage();
    }

    /**
     * Exit the application
     */
    protected void exitApp() {
        Platform.exit();
    }

    public static void showAlert(Pane root, String title, String message) {
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPrefSize(root.getWidth(), root.getHeight());

        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-border-color: #B22222; -fx-border-width: 2; "
                + "-fx-padding: 20; -fx-alignment: center; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, #8B0000, 3, 0.3, 0, 0.8);");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #B22222; -fx-font-weight: bold;");
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-font-size: 14px;-fx-padding: 5 0;");
        msgLabel.setWrapText(true);
        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-font-size: 13px; -fx-padding: 5 20; -fx-background-color: #B22222; -fx-text-fill: white;"); 
        okBtn.setOnAction(e -> root.getChildren().remove(overlay));

        box.getChildren().addAll(titleLabel, msgLabel, okBtn);

        overlay.getChildren().add(box);
        box.layoutXProperty().bind(overlay.widthProperty().subtract(box.widthProperty()).divide(2));
        box.layoutYProperty().bind(overlay.heightProperty().subtract(box.heightProperty()).divide(2));

        root.getChildren().add(overlay);
    }
    
    public static void showAlertGreen(Pane root, String title, String message) {
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPrefSize(root.getWidth(), root.getHeight());

        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-border-color: #22B255; -fx-border-width: 2; "
                + "-fx-padding: 20; -fx-alignment: center; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, #008B5B, 3, 0.3, 0, 0.8);");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #22B255; -fx-font-weight: bold;");
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-font-size: 14px;-fx-padding: 5 0;");
        msgLabel.setWrapText(true);
        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-font-size: 13px; -fx-padding: 5 20;-fx-background-color: #22B255;-fx-text-fill: white;"); 
        okBtn.setOnAction(e -> root.getChildren().remove(overlay));

        box.getChildren().addAll(titleLabel, msgLabel, okBtn);

        overlay.getChildren().add(box);
        box.layoutXProperty().bind(overlay.widthProperty().subtract(box.widthProperty()).divide(2));
        box.layoutYProperty().bind(overlay.heightProperty().subtract(box.heightProperty()).divide(2));

        root.getChildren().add(overlay);
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
    
    //use for any authentication screens
    protected void restrictToOneDigit(TextField field) {
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d?") ? change : null;
        });
        field.setTextFormatter(formatter);
    }
    // Auto movement vibes, forward and backward
    protected void autoFocus(TextField current, TextField next, TextField previous) {
        // Forward movement
        current.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() == 1) {
                if (next != null) {
                    Platform.runLater(next::requestFocus);
                }
            }
        });
        // Backward movement
        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
                if (current.getText().isEmpty()) {
                    if (previous != null) {
                        Platform.runLater(previous::requestFocus);
                    }
                } else if (current.getText().length() == 1) {
                    current.clear();
                    if (previous != null) {
                        Platform.runLater(previous::requestFocus);
                    }
                }
                event.consume();
            }
        });
    }
    //hides a pane
    protected void hidePane(Pane pane) {
        pane.setVisible(false);
        pane.setOpacity(0);
    }
    
    //use to show hidden pane when needed
    protected void showPane(Pane pane) {
        pane.setVisible(true);
        pane.setOpacity(1);
    }
}
