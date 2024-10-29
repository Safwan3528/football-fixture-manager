package com.footballfixture;

import com.footballfixture.model.Team;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TeamsController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField countryField;

    @FXML
    private TableView<Team> teamsTable;

    @FXML
    private TableColumn<Team, Integer> idColumn;

    @FXML
    private TableColumn<Team, String> nameColumn;

    @FXML
    private TableColumn<Team, String> countryColumn;

    private ObservableList<Team> teams = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());

        teamsTable.setItems(teams);
        loadTeams();

        teamsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                countryField.setText(newSelection.getCountry());
            }
        });
    }

    private void loadTeams() {
        teams.clear();
        if (DatabaseConnection.isBypassConnection()) {
            teams.add(new Team(1, "Dummy Team", "Dummy Country"));
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams")) {
                System.out.println("Connected to database successfully");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String country = rs.getString("country");
                    System.out.println("Loaded team: " + id + ", " + name + ", " + country);
                    teams.add(new Team(id, name, country));
                }
                System.out.println("Total teams loaded: " + teams.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            showAlert("Error", "Failed to load teams from database: " + e.getMessage());
        }
    }

    private void addItemWithoutAnimation(Team team) {
        teams.add(team);
    }

    @FXML
    private void addTeam() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot add team.");
            return;
        }
        String name = nameField.getText();
        String country = countryField.getText();

        if (name.isEmpty() || country.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO teams (name, country) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, country);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    addItemWithoutAnimation(new Team(generatedKeys.getInt(1), name, country));
                }
            }

            nameField.clear();
            countryField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add team to database.");
        }
    }

    @FXML
    private void updateTeam() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot update team.");
            return;
        }
        Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();
        if (selectedTeam == null) {
            showAlert("Error", "Please select a team to update.");
            return;
        }

        String name = nameField.getText();
        String country = countryField.getText();

        if (name.isEmpty() || country.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE teams SET name = ?, country = ? WHERE id = ?")) {
            pstmt.setString(1, name);
            pstmt.setString(2, country);
            pstmt.setInt(3, selectedTeam.getId());
            pstmt.executeUpdate();

            selectedTeam.setName(name);
            selectedTeam.setCountry(country);
            teamsTable.refresh();

            nameField.clear();
            countryField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update team in database.");
        }
    }

    @FXML
    private void deleteTeam() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot delete team.");
            return;
        }
        Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();
        if (selectedTeam == null) {
            showAlert("Error", "Please select a team to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM teams WHERE id = ?")) {
            pstmt.setInt(1, selectedTeam.getId());
            pstmt.executeUpdate();

            teams.remove(selectedTeam);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete team from database.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
