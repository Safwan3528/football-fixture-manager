package com.footballfixture;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVWriter;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportController {
    @FXML private CheckBox teamsCheckBox;
    @FXML private CheckBox playersCheckBox;
    @FXML private CheckBox coachesCheckBox;
    @FXML private CheckBox stadiumsCheckBox;
    @FXML private CheckBox matchesCheckBox;
    @FXML private CheckBox resultsCheckBox;
    @FXML private ProgressBar exportProgress;
    @FXML private Label statusLabel;

    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                int totalTasks = countSelectedTasks();
                int completedTasks = 0;
                
                if (teamsCheckBox.isSelected()) {
                    exportTeamsToExcel(workbook);
                    updateProgress(++completedTasks, totalTasks);
                }
                if (playersCheckBox.isSelected()) {
                    exportPlayersToExcel(workbook);
                    updateProgress(++completedTasks, totalTasks);
                }
                // ... similar for other tables
                
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                showSuccess("Data exported successfully to Excel!");
            } catch (Exception e) {
                showError("Failed to export to Excel: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportToCSV() {
        // Create a directory chooser
        FileChooser dirChooser = new FileChooser();
        dirChooser.setTitle("Choose Export Directory");
        File dir = dirChooser.showSaveDialog(null);
        
        if (dir != null) {
            try {
                int totalTasks = countSelectedTasks();
                int completedTasks = 0;
                
                if (teamsCheckBox.isSelected()) {
                    exportTeamsToCSV(new File(dir, "teams.csv"));
                    updateProgress(++completedTasks, totalTasks);
                }
                // ... similar for other tables
                
                showSuccess("Data exported successfully to CSV!");
            } catch (Exception e) {
                showError("Failed to export to CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportToSQL() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save SQL File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("SQL Files", "*.sql")
        );
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                int totalTasks = countSelectedTasks();
                int completedTasks = 0;
                
                writer.println("-- Football Fixture Management System Database Export");
                writer.println("-- Generated on: " + java.time.LocalDateTime.now());
                writer.println();
                
                if (teamsCheckBox.isSelected()) {
                    exportTeamsToSQL(writer);
                    updateProgress(++completedTasks, totalTasks);
                }
                // ... similar for other tables
                
                showSuccess("Data exported successfully to SQL!");
            } catch (Exception e) {
                showError("Failed to export to SQL: " + e.getMessage());
            }
        }
    }

    private void exportTeamsToExcel(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.createSheet("Teams");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Country");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams")) {
            
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getInt("id"));
                row.createCell(1).setCellValue(rs.getString("name"));
                row.createCell(2).setCellValue(rs.getString("country"));
            }
        }
    }

    private void exportTeamsToCSV(File file) throws SQLException, IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"ID", "Name", "Country"});
            
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams")) {
                
                while (rs.next()) {
                    writer.writeNext(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("country")
                    });
                }
            }
        }
    }

    private void exportTeamsToSQL(PrintWriter writer) throws SQLException {
        writer.println("-- Teams table");
        writer.println("INSERT INTO teams (id, name, country) VALUES");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teams")) {
            
            boolean first = true;
            while (rs.next()) {
                if (!first) writer.println(",");
                writer.printf("(%d, '%s', '%s')",
                    rs.getInt("id"),
                    rs.getString("name").replace("'", "''"),
                    rs.getString("country").replace("'", "''")
                );
                first = false;
            }
            writer.println(";");
            writer.println();
        }
    }

    private void exportPlayersToExcel(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.createSheet("Players");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Age");
        headerRow.createCell(3).setCellValue("Position");
        headerRow.createCell(4).setCellValue("Squad No");
        headerRow.createCell(5).setCellValue("Goal Score");
        headerRow.createCell(6).setCellValue("Yellow Card");
        headerRow.createCell(7).setCellValue("Red Card");
        headerRow.createCell(8).setCellValue("Club");
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT p.*, t.name as team_name FROM players p " +
                 "LEFT JOIN teams t ON p.team_id = t.id")) {
        
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getInt("id"));
                row.createCell(1).setCellValue(rs.getString("name"));
                row.createCell(2).setCellValue(rs.getInt("age"));
                row.createCell(3).setCellValue(rs.getString("position"));
                row.createCell(4).setCellValue(rs.getInt("squad_no"));
                row.createCell(5).setCellValue(rs.getInt("goal_score"));
                row.createCell(6).setCellValue(rs.getInt("yellow_card"));
                row.createCell(7).setCellValue(rs.getInt("red_card"));
                row.createCell(8).setCellValue(rs.getString("team_name"));
            }
        }
    }

    private int countSelectedTasks() {
        int count = 0;
        if (teamsCheckBox.isSelected()) count++;
        if (playersCheckBox.isSelected()) count++;
        if (coachesCheckBox.isSelected()) count++;
        if (stadiumsCheckBox.isSelected()) count++;
        if (matchesCheckBox.isSelected()) count++;
        if (resultsCheckBox.isSelected()) count++;
        return count;
    }

    private void updateProgress(int completed, int total) {
        double progress = (double) completed / total;
        exportProgress.setProgress(progress);
        statusLabel.setText(String.format("Exporting... %d/%d completed", completed, total));
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        showAlert("Success", message, Alert.AlertType.INFORMATION);
    }

    private void showError(String message) {
        statusLabel.setText("Export failed: " + message);
        showAlert("Error", message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
