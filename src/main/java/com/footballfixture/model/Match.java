package com.footballfixture.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Match {
    private final IntegerProperty id;
    private final IntegerProperty homeTeamId;
    private final IntegerProperty awayTeamId;
    private final ObjectProperty<LocalDateTime> dateTime;
    private final IntegerProperty stadiumId;

    public Match(int id, int homeTeamId, int awayTeamId, LocalDateTime dateTime, int stadiumId) {
        this.id = new SimpleIntegerProperty(id);
        this.homeTeamId = new SimpleIntegerProperty(homeTeamId);
        this.awayTeamId = new SimpleIntegerProperty(awayTeamId);
        this.dateTime = new SimpleObjectProperty<>(dateTime);
        this.stadiumId = new SimpleIntegerProperty(stadiumId);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getHomeTeamId() {
        return homeTeamId.get();
    }

    public IntegerProperty homeTeamIdProperty() {
        return homeTeamId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId.set(homeTeamId);
    }

    public int getAwayTeamId() {
        return awayTeamId.get();
    }

    public IntegerProperty awayTeamIdProperty() {
        return awayTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId.set(awayTeamId);
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    public int getStadiumId() {
        return stadiumId.get();
    }

    public IntegerProperty stadiumIdProperty() {
        return stadiumId;
    }

    public void setStadiumId(int stadiumId) {
        this.stadiumId.set(stadiumId);
    }
}
