package com.footballfixture.model;

import javafx.beans.property.*;

public class Coach {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty teamId;
    private final IntegerProperty experienceYears;
    private final StringProperty nationality;

    public Coach(int id, String name, int teamId, int experienceYears, String nationality) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.teamId = new SimpleIntegerProperty(teamId);
        this.experienceYears = new SimpleIntegerProperty(experienceYears);
        this.nationality = new SimpleStringProperty(nationality);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getTeamId() {
        return teamId.get();
    }

    public IntegerProperty teamIdProperty() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId.set(teamId);
    }

    public int getExperienceYears() {
        return experienceYears.get();
    }

    public IntegerProperty experienceYearsProperty() {
        return experienceYears;
    }

    public void setExperienceYears(int years) {
        this.experienceYears.set(years);
    }

    public String getNationality() {
        return nationality.get();
    }

    public StringProperty nationalityProperty() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality.set(nationality);
    }
}
