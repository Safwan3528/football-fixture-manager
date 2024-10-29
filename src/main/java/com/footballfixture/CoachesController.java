package com.footballfixture;

import com.footballfixture.model.Coach;
import com.footballfixture.model.Team;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CoachesController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField experienceYearsField;  // Tambah field baru

    @FXML
    private TextField nationalityField;      // Tambah field baru

    @FXML
    private ComboBox<Team> teamComboBox;

    @FXML
    private TableView<Coach> coachesTable;

    @FXML
    private TableColumn<Coach, Integer> idColumn;

    @FXML
    private TableColumn<Coach, String> nameColumn;

    @FXML
    private TableColumn<Coach, Integer> experienceYearsColumn;  // Tambah kolom baru

    @FXML
    private TableColumn<Coach, String> nationalityColumn;       // Tambah kolom baru

    @FXML
    private TableColumn<Coach, String> teamColumn;

    private ObservableList<Coach> coaches = FXCollections.observableArrayList();
    private ObservableList<Team> teams = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupTeamComboBox();
        loadTeams();
        loadCoaches();

        coachesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                experienceYearsField.setText(String.valueOf(newSelection.getExperienceYears()));
                nationalityField.setText(newSelection.getNationality());
                teamComboBox.getSelectionModel().select(
                    teams.stream()
                        .filter(t -> t.getId() == newSelection.getTeamId())
                        .findFirst()
                        .orElse(null)
                );
            }
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        experienceYearsColumn.setCellValueFactory(cellData -> cellData.getValue().experienceYearsProperty().asObject());
        nationalityColumn.setCellValueFactory(cellData -> cellData.getValue().nationalityProperty());
        teamColumn.setCellValueFactory(cellData -> {
            int teamId = cellData.getValue().getTeamId();
            Team team = teams.stream()
                .filter(t -> t.getId() == teamId)
                .findFirst()
                .orElse(null);
            return team != null ? team.nameProperty() : null;
        });

        coachesTable.setItems(coaches);
    }

    private void setupTeamComboBox() {
        teamComboBox.setItems(teams);
        
        teamComboBox.setCellFactory(param -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.getName());
                }
            }
        });

        teamComboBox.setButtonCell(new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.getName());
                }
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
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams ORDER BY name")) {
                while (rs.next()) {
                    teams.add(new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load teams from database.");
        }
    }

    private void loadCoaches() {
        coaches.clear();
        if (DatabaseConnection.isBypassConnection()) {
            coaches.add(new Coach(1, "Dummy Coach", 1, 5, "Dummy Nationality"));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM coaches")) {
                while (rs.next()) {
                    coaches.add(new Coach(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("team_id"),
                        rs.getInt("experience_years"),
                        rs.getString("nationality")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load coaches from database.");
        }
    }

    @FXML
    private void addCoach() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot add coach.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO coaches (name, team_id, experience_years, nationality) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setInt(2, teamComboBox.getValue().getId());
            pstmt.setInt(3, Integer.parseInt(experienceYearsField.getText()));
            pstmt.setString(4, nationalityField.getText());
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coaches.add(new Coach(
                        generatedKeys.getInt(1),
                        nameField.getText(),
                        teamComboBox.getValue().getId(),
                        Integer.parseInt(experienceYearsField.getText()),
                        nationalityField.getText()
                    ));
                }
            }

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add coach to database.");
        }
    }

    @FXML
    private void updateCoach() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot update coach.");
            return;
        }

        Coach selectedCoach = coachesTable.getSelectionModel().getSelectedItem();
        if (selectedCoach == null) {
            showAlert("Error", "Please select a coach to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE coaches SET name = ?, team_id = ?, experience_years = ?, nationality = ? WHERE id = ?")) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setInt(2, teamComboBox.getValue().getId());
            pstmt.setInt(3, Integer.parseInt(experienceYearsField.getText()));
            pstmt.setString(4, nationalityField.getText());
            pstmt.setInt(5, selectedCoach.getId());
            
            pstmt.executeUpdate();

            selectedCoach.setName(nameField.getText());
            selectedCoach.setTeamId(teamComboBox.getValue().getId());
            selectedCoach.setExperienceYears(Integer.parseInt(experienceYearsField.getText()));
            selectedCoach.setNationality(nationalityField.getText());
            coachesTable.refresh();

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update coach in database.");
        }
    }

    @FXML
    private void deleteCoach() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot delete coach.");
            return;
        }

        Coach selectedCoach = coachesTable.getSelectionModel().getSelectedItem();
        if (selectedCoach == null) {
            showAlert("Error", "Please select a coach to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM coaches WHERE id = ?")) {
            pstmt.setInt(1, selectedCoach.getId());
            pstmt.executeUpdate();

            coaches.remove(selectedCoach);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete coach from database.");
        }
    }

    private boolean validateInputs() {
        try {
            if (nameField.getText().isEmpty() || 
                experienceYearsField.getText().isEmpty() ||
                nationalityField.getText().isEmpty() ||
                teamComboBox.getValue() == null) {
                showAlert("Error", "Please fill in all fields.");
                return false;
            }

            int experienceYears = Integer.parseInt(experienceYearsField.getText());
            if (experienceYears < 0) {
                showAlert("Error", "Experience years cannot be negative.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Error", "Experience years must be a valid number.");
            return false;
        }
    }

    private void clearFields() {
        nameField.clear();
        experienceYearsField.clear();
        nationalityField.clear();
        teamComboBox.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
