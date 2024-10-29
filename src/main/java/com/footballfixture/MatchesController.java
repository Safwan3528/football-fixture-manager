package com.footballfixture;

import com.footballfixture.model.Match;
import com.footballfixture.model.Team;
import com.footballfixture.model.Stadium;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class MatchesController {

    @FXML
    private ComboBox<Team> homeTeamComboBox;

    @FXML
    private ComboBox<Team> awayTeamComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Stadium> stadiumComboBox;

    @FXML
    private TableView<Match> matchesTable;

    @FXML
    private TableColumn<Match, Integer> idColumn;

    @FXML
    private TableColumn<Match, String> homeTeamColumn;

    @FXML
    private TableColumn<Match, String> awayTeamColumn;

    @FXML
    private TableColumn<Match, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Match, String> stadiumColumn;

    private ObservableList<Match> matches = FXCollections.observableArrayList();
    private ObservableList<Team> teams = FXCollections.observableArrayList();
    private ObservableList<Stadium> stadiums = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        loadTeams();
        loadStadiums();
        loadMatches();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        
        homeTeamColumn.setCellValueFactory(cellData -> {
            int homeTeamId = cellData.getValue().getHomeTeamId();
            Team team = teams.stream()
                .filter(t -> t.getId() == homeTeamId)
                .findFirst()
                .orElse(null);
            return team != null ? team.nameProperty() : null;
        });
        
        awayTeamColumn.setCellValueFactory(cellData -> {
            int awayTeamId = cellData.getValue().getAwayTeamId();
            Team team = teams.stream()
                .filter(t -> t.getId() == awayTeamId)
                .findFirst()
                .orElse(null);
            return team != null ? team.nameProperty() : null;
        });
        
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());
        
        stadiumColumn.setCellValueFactory(cellData -> {
            int stadiumId = cellData.getValue().getStadiumId();
            Stadium stadium = stadiums.stream()
                .filter(s -> s.getId() == stadiumId)
                .findFirst()
                .orElse(null);
            return stadium != null ? stadium.nameProperty() : null;
        });

        matchesTable.setItems(matches);
    }

    private void setupComboBoxes() {
        // Konfigurasi tampilan untuk home team ComboBox
        homeTeamComboBox.setCellFactory(param -> new ListCell<Team>() {
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

        // Konfigurasi tampilan untuk away team ComboBox
        awayTeamComboBox.setCellFactory(param -> new ListCell<Team>() {
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

        // Konfigurasi tampilan teks yang dipilih untuk home team
        homeTeamComboBox.setButtonCell(new ListCell<Team>() {
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

        // Konfigurasi tampilan teks yang dipilih untuk away team
        awayTeamComboBox.setButtonCell(new ListCell<Team>() {
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

        // Tambahkan setup untuk stadium ComboBox
        stadiumComboBox.setCellFactory(param -> new ListCell<Stadium>() {
            @Override
            protected void updateItem(Stadium stadium, boolean empty) {
                super.updateItem(stadium, empty);
                if (empty || stadium == null) {
                    setText(null);
                } else {
                    setText(stadium.getName() + " (" + stadium.getCity() + ")");
                }
            }
        });

        // Set button cell untuk stadium ComboBox
        stadiumComboBox.setButtonCell(new ListCell<Stadium>() {
            @Override
            protected void updateItem(Stadium stadium, boolean empty) {
                super.updateItem(stadium, empty);
                if (empty || stadium == null) {
                    setText(null);
                } else {
                    setText(stadium.getName() + " (" + stadium.getCity() + ")");
                }
            }
        });
    }

    private void loadTeams() {
        teams.clear();
        if (DatabaseConnection.isBypassConnection()) {
            teams.add(new Team(1, "Dummy Home Team", "Dummy Country"));
            teams.add(new Team(2, "Dummy Away Team", "Dummy Country"));
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
            homeTeamComboBox.setItems(teams);
            awayTeamComboBox.setItems(teams);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load teams from database.");
        }
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
                 ResultSet rs = stmt.executeQuery("SELECT * FROM stadiums ORDER BY name")) {
                while (rs.next()) {
                    stadiums.add(new Stadium(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getInt("capacity")
                    ));
                }
            }
            stadiumComboBox.setItems(stadiums);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load stadiums from database.");
        }
    }

    private void loadMatches() {
        matches.clear();
        if (DatabaseConnection.isBypassConnection()) {
            matches.add(new Match(1, 1, 2, LocalDateTime.now(), 1));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            String sql = "SELECT m.*, ht.name as home_team_name, at.name as away_team_name, s.name as stadium_name " +
                        "FROM matches m " +
                        "JOIN teams ht ON m.home_team_id = ht.id " +
                        "JOIN teams at ON m.away_team_id = at.id " +
                        "JOIN stadiums s ON m.stadium_id = s.id";
            
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
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load matches from database.");
        }
    }

    @FXML
    private void addMatch() {
        if (DatabaseConnection.isBypassConnection()) {
            showAlert("Info", "Database connection is bypassed. Cannot add match.");
            return;
        }

        Team homeTeam = homeTeamComboBox.getValue();
        Team awayTeam = awayTeamComboBox.getValue();
        Stadium stadium = stadiumComboBox.getValue();
        LocalDateTime matchDate = datePicker.getValue() != null ? 
            datePicker.getValue().atStartOfDay() : null;

        if (homeTeam == null || awayTeam == null || stadium == null || matchDate == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (homeTeam.getId() == awayTeam.getId()) {
            showAlert("Error", "Home team and away team cannot be the same.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO matches (home_team_id, away_team_id, date_time, stadium_id) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, homeTeam.getId());
            pstmt.setInt(2, awayTeam.getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(matchDate));
            pstmt.setInt(4, stadium.getId());
            
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    matches.add(new Match(
                        generatedKeys.getInt(1),
                        homeTeam.getId(),
                        awayTeam.getId(),
                        matchDate,
                        stadium.getId()
                    ));
                }
            }

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add match to database.");
        }
    }

    private void clearFields() {
        homeTeamComboBox.setValue(null);
        awayTeamComboBox.setValue(null);
        stadiumComboBox.setValue(null);
        datePicker.setValue(null);
    }

    @FXML
    private void updateMatch() {
        // Implementasi untuk memperbarui pertandingan
    }

    @FXML
    private void deleteMatch() {
        // Implementasi untuk menghapus pertandingan
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
