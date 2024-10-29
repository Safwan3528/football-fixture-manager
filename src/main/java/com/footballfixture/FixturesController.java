package com.footballfixture;

import com.footballfixture.model.Fixture;
import com.footballfixture.model.Match;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FixturesController {
    @FXML private ComboBox<Match> matchComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TableView<Fixture> fixturesTable;
    @FXML private TableColumn<Fixture, Integer> idColumn;
    @FXML private TableColumn<Fixture, String> matchDetailsColumn;
    @FXML private TableColumn<Fixture, LocalDate> dateColumn;
    @FXML private TableColumn<Fixture, LocalTime> timeColumn;
    @FXML private TableColumn<Fixture, String> statusColumn;

    private ObservableList<Fixture> fixtures = FXCollections.observableArrayList();
    private ObservableList<Match> matches = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupMatchComboBox();
        loadMatches();
        loadFixtures();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        matchDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().matchDetailsProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().scheduledDateProperty());
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().scheduledTimeProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        fixturesTable.setItems(fixtures);
    }

    private void setupMatchComboBox() {
        matchComboBox.setCellFactory(param -> new ListCell<Match>() {
            @Override
            protected void updateItem(Match match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText(null);
                } else {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "SELECT ht.name as home_team, at.name as away_team " +
                                   "FROM matches m " +
                                   "JOIN teams ht ON m.home_team_id = ht.id " +
                                   "JOIN teams at ON m.away_team_id = at.id " +
                                   "WHERE m.id = ?";
                        
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, match.getId());
                        ResultSet rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                            setText(rs.getString("home_team") + " vs " + rs.getString("away_team"));
                        }
                    } catch (SQLException e) {
                        setText("Error loading match details");
                    }
                }
            }
        });

        matchComboBox.setButtonCell(new ListCell<Match>() {
            @Override
            protected void updateItem(Match match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText(null);
                } else {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "SELECT ht.name as home_team, at.name as away_team " +
                                   "FROM matches m " +
                                   "JOIN teams ht ON m.home_team_id = ht.id " +
                                   "JOIN teams at ON m.away_team_id = at.id " +
                                   "WHERE m.id = ?";
                        
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, match.getId());
                        ResultSet rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                            setText(rs.getString("home_team") + " vs " + rs.getString("away_team"));
                        }
                    } catch (SQLException e) {
                        setText("Error loading match details");
                    }
                }
            }
        });
    }

    private void loadMatches() {
        matches.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT m.* FROM matches m " +
                        "LEFT JOIN fixtures f ON m.id = f.match_id " +
                        "WHERE f.id IS NULL";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                matches.add(new Match(
                    rs.getInt("id"),
                    rs.getInt("home_team_id"),
                    rs.getInt("away_team_id"),
                    rs.getTimestamp("date_time").toLocalDateTime(),
                    rs.getInt("stadium_id")
                ));
            }
            matchComboBox.setItems(matches);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load matches.");
        }
    }

    private void loadFixtures() {
        fixtures.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT f.*, ht.name as home_team, at.name as away_team " +
                        "FROM fixtures f " +
                        "JOIN matches m ON f.match_id = m.id " +
                        "JOIN teams ht ON m.home_team_id = ht.id " +
                        "JOIN teams at ON m.away_team_id = at.id";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Fixture fixture = new Fixture(
                    rs.getInt("id"),
                    rs.getInt("match_id"),
                    rs.getDate("scheduled_date").toLocalDate(),
                    rs.getTime("scheduled_time").toLocalTime(),
                    rs.getString("status")
                );
                fixture.setMatchDetails(rs.getString("home_team") + " vs " + rs.getString("away_team"));
                fixtures.add(fixture);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load fixtures.");
        }
    }

    @FXML
    private void addFixture() {
        if (!validateInputs()) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO fixtures (match_id, scheduled_date, scheduled_time, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, matchComboBox.getValue().getId());
            pstmt.setDate(2, Date.valueOf(datePicker.getValue()));
            pstmt.setTime(3, Time.valueOf(LocalTime.parse(timeField.getText())));
            pstmt.setString(4, statusComboBox.getValue());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                loadFixtures(); // Reload all fixtures
                clearFields();
                loadMatches(); // Reload available matches
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to add fixture.");
        }
    }

    @FXML
    private void updateFixture() {
        Fixture selectedFixture = fixturesTable.getSelectionModel().getSelectedItem();
        if (selectedFixture == null) {
            showAlert("Error", "Please select a fixture to update.");
            return;
        }

        if (!validateInputs()) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE fixtures SET scheduled_date = ?, scheduled_time = ?, status = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setDate(1, Date.valueOf(datePicker.getValue()));
            pstmt.setTime(2, Time.valueOf(LocalTime.parse(timeField.getText())));
            pstmt.setString(3, statusComboBox.getValue());
            pstmt.setInt(4, selectedFixture.getId());
            
            pstmt.executeUpdate();
            loadFixtures();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update fixture.");
        }
    }

    @FXML
    private void deleteFixture() {
        Fixture selectedFixture = fixturesTable.getSelectionModel().getSelectedItem();
        if (selectedFixture == null) {
            showAlert("Error", "Please select a fixture to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fixtures WHERE id = ?");
            pstmt.setInt(1, selectedFixture.getId());
            pstmt.executeUpdate();
            
            loadFixtures();
            clearFields();
            loadMatches();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete fixture.");
        }
    }

    private boolean validateInputs() {
        if (matchComboBox.getValue() == null ||
            datePicker.getValue() == null ||
            timeField.getText().isEmpty() ||
            statusComboBox.getValue() == null) {
            showAlert("Error", "Please fill in all fields.");
            return false;
        }

        try {
            LocalTime.parse(timeField.getText());
        } catch (DateTimeParseException e) {
            showAlert("Error", "Please enter time in format HH:mm");
            return false;
        }

        return true;
    }

    private void clearFields() {
        matchComboBox.setValue(null);
        datePicker.setValue(null);
        timeField.clear();
        statusComboBox.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
