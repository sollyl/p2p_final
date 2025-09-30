package za.ac.cput.forreal;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import za.ac.cput.forreal.abstractBase.base;
import za.ac.cput.forreal.databaseManager.DBConnection;

public class TimetableController extends base implements Initializable {

    @FXML private Label username;
    @FXML private Label account_type;
    @FXML private GridPane timetableGrid;
    @FXML private ComboBox<String> moduleCodeCombo;
    @FXML private ComboBox<String> moduleNameCombo;
    @FXML private TextField roomField;
    @FXML private ColorPicker colorPicker;
    
    private String currentStudentNumber;
    private String selectedDay = "";
    private String selectedTime = "";
    private VBox selectedSlot = null;
    private Map<String, String> moduleCodeToName = new HashMap<>();
    private Map<String, SlotData> slotDataMap = new HashMap<>(); // Store all slot data

    // Inner class to store slot data
    private static class SlotData {
        String moduleCode;
        String moduleName;
        String room;
        String color;
        
        SlotData(String moduleCode, String moduleName, String room, String color) {
            this.moduleCode = moduleCode;
            this.moduleName = moduleName;
            this.room = room;
            this.color = color;
        }
    }

    @Override
public void initialize(URL url, ResourceBundle rb) {
    currentStudentNumber = getCurrentStudentNumber();
    username.setText(getCurrentUsername());
    System.out.println(getCurrentUserRole());
    account_type.setText(getCurrentUserRole());
    
    // Clear combo boxes on startup
    moduleCodeCombo.getItems().clear();
    moduleNameCombo.getItems().clear();
    moduleCodeCombo.setValue(null);
    moduleNameCombo.setValue(null);
    roomField.clear();
    colorPicker.setValue(Color.WHITE);
    
    // Load modules FIRST, then timetable data, then setup grid
    loadStudentModules();
    loadExistingTimetableData();
    setupTimetableGrid();
}

    private void setupTimetableGrid() {
        System.out.println("Setting up timetable grid with " + timetableGrid.getChildren().size() + " children");
        
        // Add click handlers to all VBox timetable slots
        for (javafx.scene.Node node : timetableGrid.getChildren()) {
            if (node instanceof VBox) {
                VBox slot = (VBox) node;
                Integer row = GridPane.getRowIndex(slot);
                Integer col = GridPane.getColumnIndex(slot);
                
                if (row != null && col != null && row > 0 && col > 0) {
                    setupSlotClickHandler(slot, row, col);
                }
            }
        }
    }

    private void setupSlotClickHandler(VBox slot, int row, int col) {
        final String day = getDayForColumn(col);
        final String time = getTimeForRow(row);
        final String slotKey = day + "_" + time;
        
        // Skip lunch slots (row 4)
        if (row == 4) {
            setupLunchSlot(slot);
            return;
        }
        
        slot.setOnMouseClicked(event -> {
            System.out.println("Slot clicked: " + day + " at " + time + " (Row: " + row + ", Col: " + col + ")");
            selectSlot(slot, day, time);
            loadSlotData(day, time);
        });
        
        // Set initial style - get data from database storage
        SlotData slotData = slotDataMap.get(slotKey);
        if (slotData != null && slotData.color != null) {
            updateSlotVisual(slot, slotData.moduleCode, slotData.room, slotData.color, false);
        } else {
            slot.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 0.8;");
        }
    }

    private void setupLunchSlot(VBox slot) {
        slot.getChildren().clear();
        Label lunchLabel = new Label("LUNCH");
        lunchLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-font-size: 12px;");
        slot.getChildren().add(lunchLabel);
        slot.setAlignment(Pos.CENTER);
        slot.setStyle("-fx-background-color: #F3F4F6; -fx-border-color: #D1D5DB; -fx-border-width: 0.5;");
        slot.setOnMouseClicked(null);
    }

    private void selectSlot(VBox slot, String day, String time) {
        // Clear previous selection but KEEP its data
        if (selectedSlot != null) {
            String previousSlotKey = selectedDay + "_" + selectedTime;
            SlotData previousData = slotDataMap.get(previousSlotKey);
            if (previousData != null && previousData.color != null) {
                updateSlotVisual(selectedSlot, previousData.moduleCode, previousData.room, previousData.color, false);
            } else {
                selectedSlot.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 0.5;");
            }
        }
        
        // Set new selection
        selectedSlot = slot;
        selectedDay = day;
        selectedTime = time;
        
        // Highlight selected slot - get data from storage
        String slotKey = day + "_" + time;
        SlotData slotData = slotDataMap.get(slotKey);
        if (slotData != null && slotData.color != null) {
            updateSlotVisual(slot, slotData.moduleCode, slotData.room, slotData.color, true);
        } else {
            slot.setStyle("-fx-background-color: white; -fx-border-color: #ffc857; -fx-border-width: 1;");
        }
        
        System.out.println("Selected: " + day + " " + time);
    }

    private void loadStudentModules() {
        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(
                 "SELECT DISTINCT m.module_code, m.module_name " +
                 "FROM modules m " +
                 "JOIN students s ON m.sub_name = s.sub_name AND m.academic_year = s.year_of_study " +
                 "WHERE s.student_number = ?")) {
            
            pstmt.setString(1, currentStudentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            moduleCodeCombo.getItems().clear();
            moduleNameCombo.getItems().clear();
            moduleCodeToName.clear();
            
            while (rs.next()) {
                String code = rs.getString("module_code");
                String name = rs.getString("module_name");
                moduleCodeCombo.getItems().add(code);
                moduleNameCombo.getItems().add(name);
                moduleCodeToName.put(code, name);
            }
            
            System.out.println("Loaded " + moduleCodeCombo.getItems().size() + " modules for student " + currentStudentNumber);
            
            // Auto-fill module name when code is selected
            moduleCodeCombo.setOnAction(event -> {
                String selectedCode = moduleCodeCombo.getValue();
                if (selectedCode != null && moduleCodeToName.containsKey(selectedCode)) {
                    moduleNameCombo.setValue(moduleCodeToName.get(selectedCode));
                } else {
                    moduleNameCombo.setValue(null);
                }
            });
            
        } catch (SQLException e) {
            System.err.println("Error loading student modules: " + e.getMessage());
            e.printStackTrace();
            showAlert((AnchorPane) timetableGrid.getParent(), "Database Error", "Failed to load modules: " + e.getMessage());
        }
    }

    private void loadSlotData(String day, String time) {
        String[] times = time.split(" - ");
        String slotKey = day + "_" + time;
        
        // Get data from our storage (which was loaded from database)
        SlotData slotData = slotDataMap.get(slotKey);
        
        if (slotData != null) {
            moduleCodeCombo.setValue(slotData.moduleCode);
            roomField.setText(slotData.room != null ? slotData.room : "");
            
            if (slotData.color != null && !slotData.color.isEmpty()) {
                try {
                    colorPicker.setValue(Color.web(slotData.color));
                } catch (Exception e) {
                    System.err.println("Invalid color format: " + slotData.color);
                    colorPicker.setValue(Color.WHITE);
                }
            } else {
                colorPicker.setValue(Color.WHITE);
            }
        } else {
            // Clear form if no data
            moduleCodeCombo.setValue(null);
            moduleNameCombo.setValue(null);
            roomField.clear();
            colorPicker.setValue(Color.WHITE);
        }
    }

    @FXML
    private void saveTimetableSlot() {
        if (selectedDay.isEmpty() || selectedTime.isEmpty() || selectedSlot == null) {
            showAlert((AnchorPane) timetableGrid.getParent(), "No Slot Selected", "Please select a timetable slot first.");
            return;
        }

        String moduleCode = moduleCodeCombo.getValue();
        if (moduleCode == null || moduleCode.trim().isEmpty()) {
            showAlert((AnchorPane) timetableGrid.getParent(), "Missing Module", "Please select a module.");
            return;
        }

        String room = roomField.getText();
        String color = colorToHex(colorPicker.getValue());
        String[] times = selectedTime.split(" - ");
        String slotKey = selectedDay + "_" + selectedTime;

        try (Connection con = DBConnection.connect()) {
            // Use ON DUPLICATE KEY UPDATE
            PreparedStatement pstmt = con.prepareStatement(
                "INSERT INTO timetable_slots (student_number, day, start_time, end_time, module_code, room, color) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE module_code = VALUES(module_code), room = VALUES(room), color = VALUES(color)");
            
            pstmt.setString(1, currentStudentNumber);
            pstmt.setString(2, selectedDay);
            pstmt.setString(3, times[0]);
            pstmt.setString(4, times[1]);
            pstmt.setString(5, moduleCode);
            pstmt.setString(6, room);
            pstmt.setString(7, color);
            
            int rowsAffected = pstmt.executeUpdate();

            // Store the data in our local map
            String moduleName = moduleCodeToName.get(moduleCode);
            slotDataMap.put(slotKey, new SlotData(moduleCode, moduleName, room, color));
            
            // Update visual - preserve selection border
            updateSlotVisual(selectedSlot, moduleCode, room, color, true);
            
            showAlertGreen((AnchorPane) timetableGrid.getParent(), "Success", "Timetable slot saved successfully!");

        } catch (SQLException e) {
            showAlert((AnchorPane) timetableGrid.getParent(), "Database Error", "Failed to save timetable slot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTimetableSlot() {
        if (selectedDay.isEmpty() || selectedTime.isEmpty() || selectedSlot == null) {
            showAlert((AnchorPane) timetableGrid.getParent(), "No Slot Selected", "Please select a timetable slot to delete.");
            return;
        }

        String[] times = selectedTime.split(" - ");
        String slotKey = selectedDay + "_" + selectedTime;

        try (Connection con = DBConnection.connect();
             PreparedStatement pstmt = con.prepareStatement(
                 "DELETE FROM timetable_slots WHERE student_number = ? AND day = ? AND start_time = ? AND end_time = ?")) {
            
            pstmt.setString(1, currentStudentNumber);
            pstmt.setString(2, selectedDay);
            pstmt.setString(3, times[0]);
            pstmt.setString(4, times[1]);

            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                // Remove from local storage
                slotDataMap.remove(slotKey);
                
                clearSlotVisual(selectedSlot);
                moduleCodeCombo.setValue(null);
                moduleNameCombo.setValue(null);
                roomField.clear();
                colorPicker.setValue(Color.WHITE);
                showAlertGreen((AnchorPane) timetableGrid.getParent(), "Success", "Timetable slot deleted successfully!");
            } else {
                showAlert((AnchorPane) timetableGrid.getParent(), "Error", "No timetable slot found to delete.");
            }

        } catch (SQLException e) {
            showAlert((AnchorPane) timetableGrid.getParent(), "Database Error", "Failed to delete timetable slot: " + e.getMessage());
            e.printStackTrace();
        }
    }
private void updateSlotVisual(VBox slot, String moduleCode, String room, String color, boolean isSelected) {
    System.out.println("Updating slot visual - Module: " + moduleCode + ", Color: " + color + ", Selected: " + isSelected);
    
    slot.getChildren().clear();
    
    VBox content = new VBox(2);
    content.setPadding(new Insets(5));
    content.setAlignment(Pos.TOP_CENTER);
    
    // Determine text color based on background brightness
    boolean isDark = isDarkColor(color);
    String textColor = isDark ? "white" : "black";
    String secondaryTextColor = isDark ? "#E5E7EB" : "#4B5563";
    String tertiaryTextColor = isDark ? "#D1D5DB" : "#6B7280";
    
    Label codeLabel = new Label(moduleCode);
    codeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + textColor + ";");
    
    String moduleName = moduleCodeToName.get(moduleCode);
    if (moduleName != null) {
        Label nameLabel = new Label(moduleName);
        nameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + secondaryTextColor + ";");
        nameLabel.setWrapText(true);
        
        if (room != null && !room.trim().isEmpty()) {
            Label roomLabel = new Label("Room: " + room);
            roomLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + tertiaryTextColor + ";");
            content.getChildren().addAll(codeLabel, nameLabel, roomLabel);
        } else {
            content.getChildren().addAll(codeLabel, nameLabel);
        }
    } else {
        if (room != null && !room.trim().isEmpty()) {
            Label roomLabel = new Label("Room: " + room);
            roomLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + tertiaryTextColor + ";");
            content.getChildren().addAll(codeLabel, roomLabel);
        } else {
            content.getChildren().add(codeLabel);
        }
    }
    
    slot.getChildren().add(content);
    
    // Apply color and selection border
    String borderStyle = isSelected ? 
        "-fx-border-color: #ffc857; -fx-border-width: 2;" : 
        "-fx-border-color: #E5E7EB; -fx-border-width: 1;";
    
    // Use white as default if no color
    String backgroundColor = (color != null && !color.isEmpty()) ? color : "white";
    slot.setStyle("-fx-background-color: " + backgroundColor + "; " + borderStyle);
    
    System.out.println("Slot style applied: " + slot.getStyle());
}
    private boolean isDarkColor(String colorHex) {
        if (colorHex == null || colorHex.length() != 7 || !colorHex.startsWith("#")) {
            return false;
        }
        
        try {
            int red = Integer.parseInt(colorHex.substring(1, 3), 16);
            int green = Integer.parseInt(colorHex.substring(3, 5), 16);
            int blue = Integer.parseInt(colorHex.substring(5, 7), 16);
            
            // Calculate relative luminance
            double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255;
            return luminance < 0.5;
        } catch (Exception e) {
            return false;
        }
    }

    private void clearSlotVisual(VBox slot) {
        slot.getChildren().clear();
        
        // Maintain selection border if this is still the selected slot
        boolean isSelected = (slot == selectedSlot);
        String borderStyle = isSelected ? 
            "-fx-border-color: #ffc857; -fx-border-width: 2;" : 
            "-fx-border-color: #E5E7EB; -fx-border-width: 1;";
            
        slot.setStyle("-fx-background-color: white; " + borderStyle);
    }

    private void loadExistingTimetableData() {
    try (Connection con = DBConnection.connect();
         PreparedStatement pstmt = con.prepareStatement(
             "SELECT day, start_time, end_time, module_code, room, color FROM timetable_slots " +
             "WHERE student_number = ?")) {
        
        pstmt.setString(1, currentStudentNumber);
        ResultSet rs = pstmt.executeQuery();
        
        int loadedCount = 0;
        while (rs.next()) {
            String day = rs.getString("day");
            String startTime = rs.getString("start_time");
            String endTime = rs.getString("end_time");
            String moduleCode = rs.getString("module_code");
            String room = rs.getString("room");
            String color = rs.getString("color");
            
            // Convert database time format (HH:mm:ss) to display format (HH:mm)
            String startTimeDisplay = startTime.substring(0, 5); // Get first 5 chars "08:30"
            String endTimeDisplay = endTime.substring(0, 5);     // Get first 5 chars "09:55"
            String timeSlot = startTimeDisplay + " - " + endTimeDisplay;
            String slotKey = day + "_" + timeSlot;
            
            System.out.println("Loading from DB: " + day + " " + timeSlot + " - " + moduleCode + " Color: " + color);
            
            // Store all the data
            String moduleName = moduleCodeToName.get(moduleCode);
            slotDataMap.put(slotKey, new SlotData(moduleCode, moduleName, room, color));
            loadedCount++;
        }
        
        System.out.println("Loaded " + loadedCount + " existing timetable entries from database");
        
        // Debug slot matching
        debugSlotMatching();
        
        // Now update the visual grid with the loaded data
        updateGridVisuals();
        
    } catch (SQLException e) {
        System.err.println("Error loading existing timetable data: " + e.getMessage());
        e.printStackTrace();
    }
}

private void updateGridVisuals() {
    System.out.println("Updating grid visuals with " + slotDataMap.size() + " entries");
    
    for (javafx.scene.Node node : timetableGrid.getChildren()) {
        if (node instanceof VBox) {
            VBox slot = (VBox) node;
            Integer row = GridPane.getRowIndex(slot);
            Integer col = GridPane.getColumnIndex(slot);
            
            if (row != null && col != null && row > 0 && col > 0) {
                String day = getDayForColumn(col);
                String time = getTimeForRow(row);
                String slotKey = day + "_" + time;
                
                // Skip lunch slots
                if (row == 4) continue;
                
                SlotData slotData = slotDataMap.get(slotKey);
                if (slotData != null && slotData.moduleCode != null) {
                    System.out.println("Updating slot: " + slotKey + " with " + slotData.moduleCode);
                    boolean isSelected = (slot == selectedSlot);
                    updateSlotVisual(slot, slotData.moduleCode, slotData.room, slotData.color, isSelected);
                } else {
                    System.out.println("No data found for slot: " + slotKey);
                }
            }
        }
    }
}

    // Helper methods
    private String getDayForColumn(int col) {
        switch (col) {
            case 1: return "MONDAY";
            case 2: return "TUESDAY";
            case 3: return "WEDNESDAY";
            case 4: return "THURSDAY";
            case 5: return "FRIDAY";
            default: return "";
        }
    }

    private String getTimeForRow(int row) {
        switch (row) {
            case 1: return "08:30 - 09:55";
            case 2: return "10:00 - 11:25";
            case 3: return "11:30 - 12:55";
            case 4: return "13:00 - 13:45";
            case 5: return "13:45 - 15:10";
            case 6: return "15:15 - 16:40";
            case 7: return "16:45 - 18:55";
            default: return "";
        }
    }

    private String colorToHex(Color color) {
        if (color == null || color.equals(Color.WHITE)) {
            return "#FFFFFF";
        }
        try {
            return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        } catch (Exception e) {
            System.err.println("Error converting color to hex: " + e.getMessage());
            return "#FFFFFF";
        }
    }
    
    private void debugSlotMatching() {
    System.out.println("=== SLOT MATCHING DEBUG ===");
    System.out.println("Slot keys in database map:");
    for (String key : slotDataMap.keySet()) {
        System.out.println("  " + key);
    }
    
    System.out.println("Slot keys being searched:");
    for (int row = 1; row <= 7; row++) {
        for (int col = 1; col <= 5; col++) {
            if (row == 4) continue; // Skip lunch
            String day = getDayForColumn(col);
            String time = getTimeForRow(row);
            String slotKey = day + "_" + time;
            System.out.println("  " + slotKey);
        }
    }
    System.out.println("=== END SLOT MATCHING DEBUG ===");
}
}