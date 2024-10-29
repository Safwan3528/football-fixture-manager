package com.footballfixture.model;

import javafx.beans.property.*;

public class MatchResult {
    private final IntegerProperty matchId;
    private final StringProperty homeTeam;
    private final StringProperty awayTeam;
    private final IntegerProperty homeTeamScore;
    private final IntegerProperty awayTeamScore;
    private final StringProperty date;

    public MatchResult(int matchId, String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore, String date) {
        this.matchId = new SimpleIntegerProperty(matchId);
        this.homeTeam = new SimpleStringProperty(homeTeam);
        this.awayTeam = new SimpleStringProperty(awayTeam);
        this.homeTeamScore = new SimpleIntegerProperty(homeTeamScore);
        this.awayTeamScore = new SimpleIntegerProperty(awayTeamScore);
        this.date = new SimpleStringProperty(date);
    }

    public int getMatchId() {
        return matchId.get();
    }

    public IntegerProperty matchIdProperty() {
        return matchId;
    }

    public String getHomeTeam() {
        return homeTeam.get();
    }

    public StringProperty homeTeamProperty() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam.get();
    }

    public StringProperty awayTeamProperty() {
        return awayTeam;
    }

    public int getHomeTeamScore() {
        return homeTeamScore.get();
    }

    public IntegerProperty homeTeamScoreProperty() {
        return homeTeamScore;
    }

    public int getAwayTeamScore() {
        return awayTeamScore.get();
    }

    public IntegerProperty awayTeamScoreProperty() {
        return awayTeamScore;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }
}
