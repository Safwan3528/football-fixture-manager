package com.footballfixture;

import com.footballfixture.model.League;
import com.footballfixture.model.Team;
import com.footballfixture.model.Match;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class LeagueController {
    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private TextField seasonField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Team> teamComboBox;
    @FXML private ComboBox<Match> matchComboBox;
    @FXML private TableView<League> leagueTable;
    @FXML private TableView<Team> leagueTeamsTable;
    @FXML private TableView<Match> leagueMatchesTable;

    @FXML private TableColumn<League, Integer> idColumn;
    @FXML private TableColumn<League, String> nameColumn;
    @FXML private TableColumn<League, String> countryColumn;
    @FXML private TableColumn<League, String> seasonColumn;
    @FXML private TableColumn<League, LocalDate> startDateColumn;
    @FXML private TableColumn<League, LocalDate> endDateColumn;

    @FXML private TableColumn<Team, String> teamNameColumn;
    @FXML private TableColumn<Team, String> teamCountryColumn;

    @FXML private TableColumn<Match, String> matchDetailsColumn;
    @FXML private TableColumn<Match, LocalDate> matchDateColumn;

    private ObservableList<League> leagues = FXCollections.observableArrayList();
    private ObservableList<Team> teams = FXCollections.observableArrayList();
    private ObservableList<Match> matches = FXCollections.observableArrayList();
    private ObservableList<Team> leagueTeams = FXCollections.observableArrayList();
    private ObservableList<Match> leagueMatches = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        loadTeams();
        loadMatches();
        loadLeagues();
    }

    private void setupTableColumns() {
        // Setup untuk tabel League
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        seasonColumn.setCellValueFactory(cellData -> cellData.getValue().seasonProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());

        // Setup untuk tabel Teams dalam League
        teamNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        teamCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());

        // Setup untuk tabel Matches dalam League
        matchDetailsColumn.setCellValueFactory(cellData -> {
            Team homeTeam = teams.stream()
                .filter(t -> t.getId() == cellData.getValue().getHomeTeamId())
                .findFirst().orElse(null);
            Team awayTeam = teams.stream()
                .filter(t -> t.getId() == cellData.getValue().getAwayTeamId())
                .findFirst().orElse(null);
            
            if (homeTeam != null && awayTeam != null) {
                return new SimpleStringProperty(
                    homeTeam.getName() + " vs " + awayTeam.getName()
                );
            }
            return new SimpleStringProperty("");
        });
        matchDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDateTime().toLocalDate()));

        leagueTable.setItems(leagues);
        leagueTeamsTable.setItems(leagueTeams);
        leagueMatchesTable.setItems(leagueMatches);
    }

    private void setupComboBoxes() {
        // Setup untuk Team ComboBox
        teamComboBox.setItems(teams);
        teamComboBox.setCellFactory(param -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                setText(empty || team == null ? "" : team.getName());
            }
        });

        // Setup untuk Match ComboBox
        matchComboBox.setItems(matches);
        matchComboBox.setCellFactory(param -> new ListCell<Match>() {
            @Override
            protected void updateItem(Match match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText("");
                } else {
                    Team homeTeam = teams.stream()
                        .filter(t -> t.getId() == match.getHomeTeamId())
                        .findFirst().orElse(null);
                    Team awayTeam = teams.stream()
                        .filter(t -> t.getId() == match.getAwayTeamId())
                        .findFirst().orElse(null);
                    
                    if (homeTeam != null && awayTeam != null) {
                        setText(homeTeam.getName() + " vs " + awayTeam.getName());
                    } else {
                        setText("Unknown Match");
                    }
                }
            }
        });
    }

    private void loadTeams() {
        teams.clear();
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
            showAlert("Error", "Failed to load teams from database.");
        }
    }

    private void loadMatches() {
        matches.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM matches ORDER BY date_time")) {
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
        } catch (SQLException e) {
            showAlert("Error", "Failed to load matches from database.");
        }
    }

    private void loadLeagues() {
        leagues.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM leagues ORDER BY name")) {
                while (rs.next()) {
                    leagues.add(new League(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("season"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load leagues from database.");
        }
    }

    @FXML
    private void addLeague() {
        if (!validateInputs()) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "INSERT INTO leagues (name, country, season, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, countryField.getText());
                pstmt.setString(3, seasonField.getText());
                pstmt.setDate(4, Date.valueOf(startDatePicker.getValue()));
                pstmt.setDate(5, Date.valueOf(endDatePicker.getValue()));
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        leagues.add(new League(
                            generatedKeys.getInt(1),
                            nameField.getText(),
                            countryField.getText(),
                            seasonField.getText(),
                            startDatePicker.getValue(),
                            endDatePicker.getValue()
                        ));
                        clearFields();
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to add league to database.");
        }
    }

    @FXML
    private void addTeamToLeague() {
        League selectedLeague = leagueTable.getSelectionModel().getSelectedItem();
        Team selectedTeam = teamComboBox.getValue();
        
        if (selectedLeague == null || selectedTeam == null) {
            showAlert("Error", "Please select both a league and a team.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "INSERT INTO league_teams (league_id, team_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selectedLeague.getId());
                pstmt.setInt(2, selectedTeam.getId());
                pstmt.executeUpdate();
                loadLeagueTeams(selectedLeague.getId());
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to add team to league.");
        }
    }

    @FXML
    private void addMatchToLeague() {
        League selectedLeague = leagueTable.getSelectionModel().getSelectedItem();
        Match selectedMatch = matchComboBox.getValue();
        
        if (selectedLeague == null || selectedMatch == null) {
            showAlert("Error", "Please select both a league and a match.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "INSERT INTO league_matches (league_id, match_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selectedLeague.getId());
                pstmt.setInt(2, selectedMatch.getId());
                pstmt.executeUpdate();
                loadLeagueMatches(selectedLeague.getId());
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to add match to league.");
        }
    }

    private void loadLeagueTeams(int leagueId) {
        leagueTeams.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "SELECT t.* FROM teams t " +
                        "JOIN league_teams lt ON t.id = lt.team_id " +
                        "WHERE lt.league_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, leagueId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    leagueTeams.add(new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country")
                    ));
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load league teams.");
        }
    }

    private void loadLeagueMatches(int leagueId) {
        leagueMatches.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "SELECT m.* FROM matches m " +
                        "JOIN league_matches lm ON m.id = lm.match_id " +
                        "WHERE lm.league_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, leagueId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    leagueMatches.add(new Match(
                        rs.getInt("id"),
                        rs.getInt("home_team_id"),
                        rs.getInt("away_team_id"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        rs.getInt("stadium_id")
                    ));
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load league matches.");
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty() || 
            countryField.getText().isEmpty() || 
            seasonField.getText().isEmpty() || 
            startDatePicker.getValue() == null || 
            endDatePicker.getValue() == null) {
            showAlert("Error", "Please fill in all fields.");
            return false;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("Error", "Start date must be before end date.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        nameField.clear();
        countryField.clear();
        seasonField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
