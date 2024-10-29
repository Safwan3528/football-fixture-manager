package com.footballfixture.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Fixture {
    private final IntegerProperty id;
    private final IntegerProperty matchId;
    private final ObjectProperty<LocalDate> scheduledDate;
    private final ObjectProperty<LocalTime> scheduledTime;
    private final StringProperty status;
    private final StringProperty matchDetails; // Untuk menampilkan detail pertandingan

    public Fixture(int id, int matchId, LocalDate scheduledDate, LocalTime scheduledTime, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.matchId = new SimpleIntegerProperty(matchId);
        this.scheduledDate = new SimpleObjectProperty<>(scheduledDate);
        this.scheduledTime = new SimpleObjectProperty<>(scheduledTime);
        this.status = new SimpleStringProperty(status);
        this.matchDetails = new SimpleStringProperty("");
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public int getMatchId() { return matchId.get(); }
    public IntegerProperty matchIdProperty() { return matchId; }
    public void setMatchId(int matchId) { this.matchId.set(matchId); }

    public LocalDate getScheduledDate() { return scheduledDate.get(); }
    public ObjectProperty<LocalDate> scheduledDateProperty() { return scheduledDate; }
    public void setScheduledDate(LocalDate date) { this.scheduledDate.set(date); }

    public LocalTime getScheduledTime() { return scheduledTime.get(); }
    public ObjectProperty<LocalTime> scheduledTimeProperty() { return scheduledTime; }
    public void setScheduledTime(LocalTime time) { this.scheduledTime.set(time); }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }

    public String getMatchDetails() { return matchDetails.get(); }
    public StringProperty matchDetailsProperty() { return matchDetails; }
    public void setMatchDetails(String details) { this.matchDetails.set(details); }
}
