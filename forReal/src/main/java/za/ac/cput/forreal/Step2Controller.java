package za.ac.cput.forreal;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;
import za.ac.cput.forreal.databaseManager.services.MarkValidationservice;

public class Step2Controller extends base implements Initializable {

    private String currentStudentNumber;
    private String studentCourse;
    private int studentYear;
    private List<String> availableModules = new ArrayList<>();
    private Set<String> enteredModules = new HashSet<>();

    @FXML
    private ScrollPane scroller;
    @FXML
    private ComboBox<?> combo_2;
    @FXML
    private VBox module_box;
    @FXML
    private VBox manualBox;
    @FXML
    private AnchorPane main;
    @FXML
    private Button delete_man;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        verticalScroll(scroller);
        currentStudentNumber = getCurrentStudentNumber();
        getStudentDetails();
        loadAvailableModules();

        if (delete_man != null) {
            delete_man.setOnAction(e -> {
                if (manualBox != null && module_box != null) {
                    module_box.getChildren().remove(manualBox);
                }
            });
        }
    }

    private void getStudentDetails() {
        String sql = "SELECT sub_name, year_of_study FROM students WHERE student_number = ?";

        try (var con = DBConnection.connect(); var pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, currentStudentNumber);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                studentCourse = rs.getString("sub_name");
                studentYear = rs.getInt("year_of_study");
            }

        } catch (Exception e) {
            System.err.println("Error getting student details: " + e.getMessage());
        }
    }

    private void loadAvailableModules() {
        String sql = "SELECT m.module_code, m.module_name "
                + "FROM modules m "
                + "JOIN marks mk ON m.module_code = mk.module_code "
                + "WHERE mk.student_number = ?";

        try (var con = DBConnection.connect(); var pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, currentStudentNumber);
            var rs = pstmt.executeQuery();

            availableModules.clear();
            while (rs.next()) {
                String moduleCode = rs.getString("module_code");
                String moduleName = rs.getString("module_name");
                availableModules.add(moduleCode + " - " + moduleName);
            }

            // Populate the first combo box
            if (manualBox != null) {
                @SuppressWarnings("unchecked")
                ComboBox<String> combo = (ComboBox<String>) manualBox.lookup("#combo_2");
                if (combo != null) {
                    combo.getItems().clear();
                    combo.getItems().addAll(availableModules);
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading available modules: " + e.getMessage());
        }
    }


    @FXML
    private void step1(MouseEvent event) throws IOException {
        loadScene("step1.fxml");
    }
    
    @FXML
    private void step3(MouseEvent event) throws IOException{
        if (validateAndSaveMarks()) {
            loadScene("step3.fxml");
        }
    }

    @FXML
    private void skipThrough(MouseEvent event) throws IOException {
        // Only first years can skip
        if (studentYear == 1) {
            loadScene("step3.fxml");
        } else {
            showAlert(main, "Cannot Skip", "As a " + studentYear + " year student, you must enter your module marks.");
        }
    }

    @FXML
    private void deleteModule(MouseEvent event) {
    }

    @FXML
    private void addModules(MouseEvent event) {
    }
    //ozll gyez vskc unvx

    @FXML
    private void addRow(ActionEvent event) {
        // Create new module input row
        VBox container = createModuleInputRow();
        module_box.getChildren().add(container);
    }

    private VBox createModuleInputRow() {
        // Label
        Label choose_mod = new Label("Choose your module");
        choose_mod.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-font-family: \"Sans-Serif\";");

        // ComboBox with available modules
        ComboBox<String> moduleCombo = new ComboBox<>();
        moduleCombo.getItems().addAll(availableModules);
        moduleCombo.getStyleClass().add("log_field");
        moduleCombo.getStyleClass().add("box_text");
        moduleCombo.getStyleClass().add("step_combo");
        moduleCombo.setMinHeight(34);
        moduleCombo.setPrefWidth(295);
        moduleCombo.setMaxWidth(295);
        moduleCombo.setMinWidth(295);
        moduleCombo.setId("combo_2");

        // Mark field
        TextField markField = new TextField();
        markField.getStyleClass().add("log_field");
        markField.getStyleClass().add("step_mark");
        markField.setPromptText("Mark");
        markField.setStyle("-fx-alignment: center;");
        
        markField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow empty input so the user can still type
            if (newText.isEmpty()) {
                return change;
            }

            // Must be numeric only
            try {
                int value = Integer.parseInt(newText);
                if (value < 0 || value > 100) {
                    return null; // reject input
                }
            } catch (NumberFormatException e) {
                return null; // reject non-numeric input
            }

            return change; // accept valid input
        }));
        markField.setMinWidth(45);
        markField.setMaxWidth(45);
        markField.setMinHeight(34);

        // Delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.getStyleClass().add("but");
        deleteBtn.setMinWidth(64);
        deleteBtn.setMaxWidth(64);
        deleteBtn.setMinHeight(34);

        HBox inputRow = new HBox(17);
        inputRow.getChildren().addAll(moduleCombo, markField, deleteBtn);
        inputRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox container = new VBox(6);
        container.getChildren().addAll(choose_mod, inputRow);
        container.setPadding(new javafx.geometry.Insets(0, 0, 3, 0));

        deleteBtn.setOnAction(e -> module_box.getChildren().remove(container));

        return container;
    }

    private Set<String> getRequiredModules() {
        Set<String> required = new HashSet<>();

        String sql = "SELECT module_code FROM modules WHERE sub_name = ? AND academic_year >= ?";

        try (var con = DBConnection.connect(); var pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, studentCourse);
            pstmt.setInt(2, studentYear);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                required.add(rs.getString("module_code"));
            }

        } catch (Exception e) {
            System.err.println("Error getting required modules: " + e.getMessage());
        }

        return required;
    }

    private boolean validateAndSaveMarks() {
        enteredModules.clear();
        boolean allValid = true;
        List<String> validationErrors = new ArrayList<>();
        Set<String> missingModules = new HashSet<>(getRequiredModules());

        // First pass: Validate all entered modules
        for (var container : module_box.getChildren()) {
            if (container instanceof VBox) {
                VBox vbox = (VBox) container;
                HBox inputRow = (HBox) vbox.getChildren().get(1);

                ComboBox<String> moduleCombo = (ComboBox<String>) inputRow.getChildren().get(0);
                TextField markField = (TextField) inputRow.getChildren().get(1);

                // Check if both fields are filled
                if (moduleCombo.getValue() == null || markField.getText().isEmpty()) {
                    validationErrors.add("Please fill in all module selections and marks");
                    allValid = false;
                    return false;
                }

                String moduleCode = moduleCombo.getValue().split(" - ")[0];
                String markText = markField.getText();

                // Validate mark format
                try {
                    int mark = Integer.parseInt(markText);
                    if (mark < 0 || mark > 100) {
                        validationErrors.add("Mark must be between 0 and 100 for " + moduleCode);
                        allValid = false;
                        continue;
                    }

                    // Validate against database record
                    if (!MarkValidationservice.validateMarksAgainstDatabase(currentStudentNumber, moduleCode, mark)) {
                        validationErrors.add("The mark entered for " + moduleCode + " doesn't match our records");
                        allValid = false;
                        continue;
                    }

                    // If validation passed, save and track
                    saveMarkToDatabase(moduleCode, mark);
                    enteredModules.add(moduleCode);
                    missingModules.remove(moduleCode);

                } catch (NumberFormatException e) {
                    validationErrors.add("Please enter a valid number for " + moduleCode);
                    allValid = false;
                }
            }
        }

        // Check for missing required modules (only for years 2+)
        if (studentYear > 1 && !missingModules.isEmpty()) {
            StringBuilder missingList = new StringBuilder("Missing required modules:\n");
            for (String module : missingModules) {
                // Get module name for better user feedback
                String moduleName = getModuleName(module);
                missingList.append("• ").append(module).append(" - ").append(moduleName).append("\n");
            }
            validationErrors.add(missingList.toString());
            allValid = false;
        }

        // Show all validation errors at once
        if (!allValid) {
            StringBuilder errorMessage = new StringBuilder();
            for (String error : validationErrors) {
                errorMessage.append("• ").append(error).append("\n\n");
            }
            showAlert(main, "Validation Errors", errorMessage.toString());
        }

        return allValid;
    }

    private void saveMarkToDatabase(String moduleCode, int mark) {
        String sql = "INSERT INTO marks (student_number, module_code, mark) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, currentStudentNumber);
            pstmt.setString(2, moduleCode);
            pstmt.setInt(3, mark);

            pstmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error saving mark: " + e.getMessage());
        }
    }

    private String getModuleName(String moduleCode) {
        String sql = "SELECT module_name FROM modules WHERE module_code = ?";

        try (var con = DBConnection.connect(); var pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, moduleCode);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("module_name");
            }

        } catch (Exception e) {
            System.err.println("Error getting module name: " + e.getMessage());
        }
        return moduleCode; // Fallback to code if name not found
    }
}
