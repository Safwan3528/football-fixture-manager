package com.footballfixture;

import com.footballfixture.model.Stadium;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class StadiumsController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField capacityField;

    @FXML
    private TableView<Stadium> stadiumsTable;

    @FXML
    private TableColumn<Stadium, Integer> idColumn;

    @FXML
    private TableColumn<Stadium, String> nameColumn;

    @FXML
    private TableColumn<Stadium, String> cityColumn;

    @FXML
    private TableColumn<Stadium, Integer> capacityColumn;

    private ObservableList<Stadium> stadiums = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadStadiums();

        stadiumsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                cityField.setText(newSelection.getCity());
                capacityField.setText(String.valueOf(newSelection.getCapacity()));
            }
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacityProperty().asObject());

        stadiumsTable.setItems(stadiums);
    }

    private void loadStadiums() {
        stadiums.clear();
        if (DatabaseConnection.isBypassConnection()) {
            stadiums.add(new Stadium(1, "Dummy Stadium", "Dummy City", 50000));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM stadiums")) {
                while (rs.next()) {
                    stadiums.add(new Stadium(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getInt("capacity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load stadiums from database.");
        }
    }

    @FXML
    private void addStadium() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot add stadium.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO stadiums (name, city, capacity) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, cityField.getText());
            pstmt.setInt(3, Integer.parseInt(capacityField.getText()));
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stadiums.add(new Stadium(
                        generatedKeys.getInt(1),
                        nameField.getText(),
                        cityField.getText(),
                        Integer.parseInt(capacityField.getText())
                    ));
                }
            }

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add stadium to database.");
        }
    }

    @FXML
    private void updateStadium() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot update stadium.");
            return;
        }

        Stadium selectedStadium = stadiumsTable.getSelectionModel().getSelectedItem();
        if (selectedStadium == null) {
            showAlert("Error", "Please select a stadium to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE stadiums SET name = ?, city = ?, capacity = ? WHERE id = ?")) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, cityField.getText());
            pstmt.setInt(3, Integer.parseInt(capacityField.getText()));
            pstmt.setInt(4, selectedStadium.getId());
            
            pstmt.executeUpdate();

            selectedStadium.setName(nameField.getText());
            selectedStadium.setCity(cityField.getText());
            selectedStadium.setCapacity(Integer.parseInt(capacityField.getText()));
            stadiumsTable.refresh();

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update stadium in database.");
        }
    }

    @FXML
    private void deleteStadium() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot delete stadium.");
            return;
        }

        Stadium selectedStadium = stadiumsTable.getSelectionModel().getSelectedItem();
        if (selectedStadium == null) {
            showAlert("Error", "Please select a stadium to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM stadiums WHERE id = ?")) {
            pstmt.setInt(1, selectedStadium.getId());
            pstmt.executeUpdate();

            stadiums.remove(selectedStadium);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete stadium from database.");
        }
    }

    private boolean validateInputs() {
        try {
            if (nameField.getText().isEmpty() || cityField.getText().isEmpty() ||
                capacityField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return false;
            }

            int capacity = Integer.parseInt(capacityField.getText());
            if (capacity <= 0) {
                showAlert("Error", "Capacity must be a positive number.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Error", "Capacity must be a valid number.");
            return false;
        }
    }

    private void clearFields() {
        nameField.clear();
        cityField.clear();
        capacityField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
