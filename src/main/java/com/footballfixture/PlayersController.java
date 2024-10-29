package com.footballfixture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.footballfixture.model.Player;
import com.footballfixture.model.Team;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PlayersController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField squadNoField;

    @FXML
    private TextField goalScoreField;

    @FXML
    private TextField yellowCardField;

    @FXML
    private TextField redCardField;

    @FXML
    private ComboBox<Team> teamComboBox;

    @FXML
    private TableView<Player> playersTable;

    @FXML
    private TableColumn<Player, Integer> idColumn;

    @FXML
    private TableColumn<Player, String> nameColumn;

    @FXML
    private TableColumn<Player, Integer> ageColumn;

    @FXML
    private TableColumn<Player, String> positionColumn;

    @FXML
    private TableColumn<Player, String> teamColumn;

    @FXML
    private TableColumn<Player, Integer> squadNoColumn;

    @FXML
    private TableColumn<Player, Integer> goalScoreColumn;

    @FXML
    private TableColumn<Player, Integer> yellowCardColumn;

    @FXML
    private TableColumn<Player, Integer> redCardColumn;

    private ObservableList<Player> players = FXCollections.observableArrayList();
    private ObservableList<Team> teams = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupTeamComboBoxListener();
        loadTeams();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().ageProperty().asObject());
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        teamColumn.setCellValueFactory(cellData -> {
            int teamId = cellData.getValue().getTeamId();
            Team team = teams.stream().filter(t -> t.getId() == teamId).findFirst().orElse(null);
            return team != null ? team.nameProperty() : null;
        });
        squadNoColumn.setCellValueFactory(cellData -> cellData.getValue().squadNoProperty().asObject());
        goalScoreColumn.setCellValueFactory(cellData -> cellData.getValue().goalScoreProperty().asObject());
        yellowCardColumn.setCellValueFactory(cellData -> cellData.getValue().yellowCardProperty().asObject());
        redCardColumn.setCellValueFactory(cellData -> cellData.getValue().redCardProperty().asObject());

        playersTable.setItems(players);
        teamComboBox.setItems(teams);

        playersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                ageField.setText(String.valueOf(newSelection.getAge()));
                positionField.setText(newSelection.getPosition());
                squadNoField.setText(String.valueOf(newSelection.getSquadNo()));
                goalScoreField.setText(String.valueOf(newSelection.getGoalScore()));
                yellowCardField.setText(String.valueOf(newSelection.getYellowCard()));
                redCardField.setText(String.valueOf(newSelection.getRedCard()));
                teamComboBox.getSelectionModel().select(
                    teams.stream()
                        .filter(t -> t.getId() == newSelection.getTeamId())
                        .findFirst()
                        .orElse(null)
                );
            }
        });
    }

    private void setupTeamComboBoxListener() {
        // Konfigurasi tampilan ComboBox untuk menampilkan nama club
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

        // Konfigurasi tampilan teks yang dipilih
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

        // Listener untuk memuat pemain ketika club dipilih
        teamComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadPlayersForTeam(newVal.getId());
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
                 ResultSet rs = stmt.executeQuery("SELECT * FROM teams ORDER BY name")) { // Menambahkan ORDER BY untuk mengurutkan berdasarkan nama
                while (rs.next()) {
                    teams.add(new Team(rs.getInt("id"), rs.getString("name"), rs.getString("country")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load teams from database.");
        }
    }

    private void loadPlayersForTeam(int teamId) {
        players.clear();
        if (DatabaseConnection.isBypassConnection()) {
            players.add(new Player(1, "Dummy Player", 25, "Forward", teamId, 10, 5, 0, 0));
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM players WHERE team_id = ?")) {
                pstmt.setInt(1, teamId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    players.add(new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("position"),
                        rs.getInt("team_id"),
                        rs.getInt("squad_no"),
                        rs.getInt("goal_score"),
                        rs.getInt("yellow_card"),
                        rs.getInt("red_card")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load players from database.");
        }
    }

    @FXML
    private void addPlayer() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot add player.");
            return;
        }

        Team selectedTeam = teamComboBox.getValue();
        if (selectedTeam == null) {
            showAlert("Error", "Please select a club first.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO players (name, age, position, team_id, squad_no, goal_score, yellow_card, red_card) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setInt(2, Integer.parseInt(ageField.getText()));
            pstmt.setString(3, positionField.getText());
            pstmt.setInt(4, selectedTeam.getId());
            pstmt.setInt(5, Integer.parseInt(squadNoField.getText()));
            pstmt.setInt(6, Integer.parseInt(goalScoreField.getText()));
            pstmt.setInt(7, Integer.parseInt(yellowCardField.getText()));
            pstmt.setInt(8, Integer.parseInt(redCardField.getText()));
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    players.add(new Player(
                        generatedKeys.getInt(1),
                        nameField.getText(),
                        Integer.parseInt(ageField.getText()),
                        positionField.getText(),
                        selectedTeam.getId(),
                        Integer.parseInt(squadNoField.getText()),
                        Integer.parseInt(goalScoreField.getText()),
                        Integer.parseInt(yellowCardField.getText()),
                        Integer.parseInt(redCardField.getText())
                    ));
                }
            }

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add player to database.");
        }
    }

    @FXML
    private void updatePlayer() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot update player.");
            return;
        }

        Player selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            showAlert("Error", "Please select a player to update.");
            return;
        }

        Team selectedTeam = teamComboBox.getValue();
        if (selectedTeam == null) {
            showAlert("Error", "Please select a club.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE players SET name = ?, age = ?, position = ?, team_id = ?, squad_no = ?, goal_score = ?, yellow_card = ?, red_card = ? WHERE id = ?")) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setInt(2, Integer.parseInt(ageField.getText()));
            pstmt.setString(3, positionField.getText());
            pstmt.setInt(4, selectedTeam.getId());
            pstmt.setInt(5, Integer.parseInt(squadNoField.getText()));
            pstmt.setInt(6, Integer.parseInt(goalScoreField.getText()));
            pstmt.setInt(7, Integer.parseInt(yellowCardField.getText()));
            pstmt.setInt(8, Integer.parseInt(redCardField.getText()));
            pstmt.setInt(9, selectedPlayer.getId());
            
            pstmt.executeUpdate();

            loadPlayersForTeam(selectedTeam.getId());
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update player in database.");
        }
    }

    @FXML
    private void deletePlayer() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot delete player.");
            return;
        }

        Player selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            showAlert("Error", "Please select a player to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM players WHERE id = ?")) {
            pstmt.setInt(1, selectedPlayer.getId());
            pstmt.executeUpdate();

            players.remove(selectedPlayer);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete player from database.");
        }
    }

    private boolean validateInputs() {
        try {
            if (nameField.getText().isEmpty() || positionField.getText().isEmpty() ||
                ageField.getText().isEmpty() || squadNoField.getText().isEmpty() ||
                goalScoreField.getText().isEmpty() || yellowCardField.getText().isEmpty() ||
                redCardField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return false;
            }

            int age = Integer.parseInt(ageField.getText());
            int squadNo = Integer.parseInt(squadNoField.getText());
            int goalScore = Integer.parseInt(goalScoreField.getText());
            int yellowCard = Integer.parseInt(yellowCardField.getText());
            int redCard = Integer.parseInt(redCardField.getText());

            if (age < 0 || squadNo < 0 || goalScore < 0 || yellowCard < 0 || redCard < 0) {
                showAlert("Error", "All numeric fields must be positive numbers.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Error", "All numeric fields must be valid numbers.");
            return false;
        }
    }

    private void clearFields() {
        nameField.clear();
        ageField.clear();
        positionField.clear();
        squadNoField.clear();
        goalScoreField.clear();
        yellowCardField.clear();
        redCardField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
