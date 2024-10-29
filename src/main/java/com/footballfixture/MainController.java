package com.footballfixture;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void showDatabaseConnectionDialog() {
        DatabaseConnectionDialog dialog = new DatabaseConnectionDialog();
        dialog.showAndWait();

        try {
            DatabaseConnection.setBypassConnection(false);
            DatabaseConnection.initializeDatabase();
            showAlert("Connection Successful", "Successfully connected to the database.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Connection Failed", "Failed to connect to the database: " + e.getMessage(), Alert.AlertType.ERROR);
            DatabaseConnection.setBypassConnection(true);
        }
    }

    @FXML
    private void exitApplication() {
        Platform.exit();
    }

    @FXML
    private void showTeamsView() {
        loadView("teams.fxml");
    }

    @FXML
    private void showPlayersView() {
        loadView("players.fxml");
    }

    @FXML
    private void showMatchesView() {
        loadView("matches.fxml");
    }

    @FXML
    private void showCoachesView() {
        loadView("coaches.fxml");
    }

    @FXML
    private void showStadiumsView() {
        loadView("stadiums.fxml");
    }

    @FXML
    private void showResultsView() {
        loadView("results.fxml"); 
    }

    @FXML
    private void showFixturesView() {
        loadView("fixtures.fxml");
    }

    @FXML
    private void showLeagueView() {
        loadView("leagues.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxmlFile));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load view: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Football Fixture Management");
        alert.setContentText("Version 1.0\nDeveloped by Safwan Rahimi");
        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void clearDatabase() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Clear Database");
        confirmDialog.setHeaderText("Clear Database Confirmation");
        confirmDialog.setContentText("Are you sure you want to clear all data from the database? This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Tutup semua connection yang ada
                DatabaseConnection.closeAllConnections();
                
                // Clear database
                DatabaseConnection.deleteDatabase();
                
                // Tunggu sebentar untuk memastikan file betul - betul clear
                Thread.sleep(1000);
                
                // Install semula database
                DatabaseConnection.initializeDatabase();
                
                showAlert("Success", "Database has been cleared successfully.", Alert.AlertType.INFORMATION);
                
                // Refresh current view
                String currentView = getCurrentView();
                if (currentView != null) {
                    loadView(currentView);
                }
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to clear database: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private String getCurrentView() {
        if (contentArea.getChildren().isEmpty()) {
            return null;
        }
        
        Node currentContent = contentArea.getChildren().get(0);
        String id = currentContent.getId();
        
        if (id == null) {
            return null;
        }
        
        // Map the view ID to its FXML file
        switch (id) {
            case "teamsView": return "teams.fxml";
            case "playersView": return "players.fxml";
            case "matchesView": return "matches.fxml";
            case "coachesView": return "coaches.fxml";
            case "stadiumsView": return "stadiums.fxml";
            case "resultsView": return "results.fxml";
            case "fixturesView": return "fixtures.fxml";
            default: return null;
        }
    }
}
