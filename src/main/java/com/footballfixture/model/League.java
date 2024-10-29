package com.footballfixture.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class League {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty country;
    private final StringProperty season;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;

    public League(int id, String name, String country, String season, LocalDate startDate, LocalDate endDate) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.country = new SimpleStringProperty(country);
        this.season = new SimpleStringProperty(season);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getCountry() { return country.get(); }
    public StringProperty countryProperty() { return country; }
    public void setCountry(String country) { this.country.set(country); }

    public String getSeason() { return season.get(); }
    public StringProperty seasonProperty() { return season; }
    public void setSeason(String season) { this.season.set(season); }

    public LocalDate getStartDate() { return startDate.get(); }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public void setStartDate(LocalDate date) { this.startDate.set(date); }

    public LocalDate getEndDate() { return endDate.get(); }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }
    public void setEndDate(LocalDate date) { this.endDate.set(date); }
}
