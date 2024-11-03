package com.footballfixture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.footballfixture.model.Match;
import com.footballfixture.model.MatchResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ResultsController {

    @FXML
    private ComboBox<Match> matchComboBox;

    @FXML
    private TextField homeScoreField;

    @FXML
    private TextField awayScoreField;

    @FXML
    private TableView<MatchResult> resultsTable;

    @FXML
    private TableColumn<MatchResult, Integer> matchIdColumn;

    @FXML
    private TableColumn<MatchResult, String> homeTeamColumn;

    @FXML
    private TableColumn<MatchResult, Integer> homeScoreColumn;

    @FXML
    private TableColumn<MatchResult, String> awayTeamColumn;

    @FXML
    private TableColumn<MatchResult, Integer> awayScoreColumn;

    @FXML
    private TableColumn<MatchResult, String> dateColumn;

    private ObservableList<MatchResult> results = FXCollections.observableArrayList();
    private ObservableList<Match> matches = FXCollections.observableArrayList();

    private void setupComboBox() {
        // config tampilan untuk match ComboBox
        matchComboBox.setCellFactory(param -> new ListCell<Match>() {
            @Override
            protected void updateItem(Match match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText(null);
                } else {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        if (conn != null) {
                            String sql = "SELECT ht.name as home_team, at.name as away_team, " +
                                       "s.name as stadium_name, m.date_time " +
                                       "FROM matches m " +
                                       "JOIN teams ht ON m.home_team_id = ht.id " +
                                       "JOIN teams at ON m.away_team_id = at.id " +
                                       "JOIN stadiums s ON m.stadium_id = s.id " +
                                       "WHERE m.id = ?";
                            
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setInt(1, match.getId());
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                    String homeTeam = rs.getString("home_team");
                                    String awayTeam = rs.getString("away_team");
                                    String stadium = rs.getString("stadium_name");
                                    String dateTime = rs.getTimestamp("date_time").toString();
                                    setText(String.format("%s vs %s at %s (%s)", 
                                        homeTeam, awayTeam, stadium, dateTime));
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        setText("Error loading match details");
                    }
                }
            }
        });

        // Set button cell untuk match ComboBox
        matchComboBox.setButtonCell(new ListCell<Match>() {
            @Override
            protected void updateItem(Match match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText(null);
                } else {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        if (conn != null) {
                            String sql = "SELECT ht.name as home_team, at.name as away_team, " +
                                       "s.name as stadium_name, m.date_time " +
                                       "FROM matches m " +
                                       "JOIN teams ht ON m.home_team_id = ht.id " +
                                       "JOIN teams at ON m.away_team_id = at.id " +
                                       "JOIN stadiums s ON m.stadium_id = s.id " +
                                       "WHERE m.id = ?";
                            
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setInt(1, match.getId());
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                    String homeTeam = rs.getString("home_team");
                                    String awayTeam = rs.getString("away_team");
                                    String stadium = rs.getString("stadium_name");
                                    String dateTime = rs.getTimestamp("date_time").toString();
                                    setText(String.format("%s vs %s at %s (%s)", 
                                        homeTeam, awayTeam, stadium, dateTime));
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        setText("Error loading match details");
                    }
                }
            }
        });
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBox();
        loadMatches();
        loadResults();

        // Menambahkan listener untuk pemilihan table
        resultsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Match selectedMatch = matches.stream()
                    .filter(m -> m.getId() == newSelection.getMatchId())
                    .findFirst()
                    .orElse(null);
                matchComboBox.setValue(selectedMatch);
                homeScoreField.setText(String.valueOf(newSelection.getHomeTeamScore()));
                awayScoreField.setText(String.valueOf(newSelection.getAwayTeamScore()));
            }
        });
    }

    private void setupTableColumns() {
        matchIdColumn.setCellValueFactory(cellData -> cellData.getValue().matchIdProperty().asObject());
        homeTeamColumn.setCellValueFactory(cellData -> cellData.getValue().homeTeamProperty());
        homeScoreColumn.setCellValueFactory(cellData -> cellData.getValue().homeTeamScoreProperty().asObject());
        awayTeamColumn.setCellValueFactory(cellData -> cellData.getValue().awayTeamProperty());
        awayScoreColumn.setCellValueFactory(cellData -> cellData.getValue().awayTeamScoreProperty().asObject());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        resultsTable.setItems(results);
    }

    private void loadMatches() {
        matches.clear();
        if (DatabaseConnection.isBypassConnection()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "SELECT m.* FROM matches m " +
                        "LEFT JOIN match_results mr ON m.id = mr.match_id " +
                        "WHERE mr.id IS NULL " +
                        "ORDER BY m.date_time";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    matches.add(new Match(
                        rs.getInt("id"),
                        rs.getInt("home_team_id"),
                        rs.getInt("away_team_id"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        rs.getInt("stadium_id")
                    ));
                }
            }
            matchComboBox.setItems(matches);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load matches from database.");
        }
    }

    private void loadResults() {
        results.clear();
        if (DatabaseConnection.isBypassConnection()) {
            results.add(new MatchResult(1, "Team A", "Team B", 2, 1, "2024-01-01"));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "SELECT mr.*, m.date_time, ht.name as home_team_name, at.name as away_team_name " +
                        "FROM match_results mr " +
                        "JOIN matches m ON mr.match_id = m.id " +
                        "JOIN teams ht ON m.home_team_id = ht.id " +
                        "JOIN teams at ON m.away_team_id = at.id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    results.add(new MatchResult(
                        rs.getInt("match_id"),
                        rs.getString("home_team_name"),
                        rs.getString("away_team_name"),
                        rs.getInt("home_team_score"),
                        rs.getInt("away_team_score"),
                        rs.getTimestamp("date_time").toString()
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load results from database.");
        }
    }

    @FXML
    private void addResult() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot add result.");
            return;
        }

        Match selectedMatch = matchComboBox.getValue();
        if (selectedMatch == null) {
            showAlert("Error", "Please select a match.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO match_results (match_id, home_team_score, away_team_score) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, selectedMatch.getId());
            pstmt.setInt(2, Integer.parseInt(homeScoreField.getText()));
            pstmt.setInt(3, Integer.parseInt(awayScoreField.getText()));
            
            pstmt.executeUpdate();
            loadResults();
            clearFields();
            loadMatches(); // Reload matches to remove the one that just got a result
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add result to database.");
        }
    }

    @FXML
    private void updateResult() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot update result.");
            return;
        }

        MatchResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
        if (selectedResult == null) {
            showAlert("Error", "Please select a result to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE match_results SET home_team_score = ?, away_team_score = ? WHERE match_id = ?")) {
            
            pstmt.setInt(1, Integer.parseInt(homeScoreField.getText()));
            pstmt.setInt(2, Integer.parseInt(awayScoreField.getText()));
            pstmt.setInt(3, selectedResult.getMatchId());
            
            pstmt.executeUpdate();
            loadResults();
            clearFields();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update result in database.");
        }
    }

    @FXML
    private void deleteResult() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot delete result.");
            return;
        }

        MatchResult selectedResult = resultsTable.getSelectionModel().getSelectedItem();
        if (selectedResult == null) {
            showAlert("Error", "Please select a result to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM match_results WHERE match_id = ?")) {
            pstmt.setInt(1, selectedResult.getMatchId());
            pstmt.executeUpdate();

            loadResults();
            clearFields();
            loadMatches(); // Reload matches as this match can now have a new result
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete result from database.");
        }
    }

    private boolean validateInputs() {
        try {
            if (homeScoreField.getText().isEmpty() || awayScoreField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all score fields.");
                return false;
            }

            int homeScore = Integer.parseInt(homeScoreField.getText());
            int awayScore = Integer.parseInt(awayScoreField.getText());

            if (homeScore < 0 || awayScore < 0) {
                showAlert("Error", "Scores cannot be negative.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for scores.");
            return false;
        }
    }

    private void clearFields() {
        matchComboBox.setValue(null);
        homeScoreField.clear();
        awayScoreField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
