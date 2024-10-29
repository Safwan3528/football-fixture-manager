package com.footballfixture.model;

import javafx.beans.property.*;

public class Player {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty age;
    private final StringProperty position;
    private final IntegerProperty teamId;
    private final IntegerProperty squadNo;
    private final IntegerProperty goalScore;
    private final IntegerProperty yellowCard;
    private final IntegerProperty redCard;

    public Player(int id, String name, int age, String position, int teamId, int squadNo, int goalScore, int yellowCard, int redCard) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.age = new SimpleIntegerProperty(age);
        this.position = new SimpleStringProperty(position);
        this.teamId = new SimpleIntegerProperty(teamId);
        this.squadNo = new SimpleIntegerProperty(squadNo);
        this.goalScore = new SimpleIntegerProperty(goalScore);
        this.yellowCard = new SimpleIntegerProperty(yellowCard);
        this.redCard = new SimpleIntegerProperty(redCard);
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

    public int getAge() {
        return age.get();
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public String getPosition() {
        return position.get();
    }

    public StringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
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

    public int getSquadNo() {
        return squadNo.get();
    }

    public IntegerProperty squadNoProperty() {
        return squadNo;
    }

    public void setSquadNo(int squadNo) {
        this.squadNo.set(squadNo);
    }

    public int getGoalScore() {
        return goalScore.get();
    }

    public IntegerProperty goalScoreProperty() {
        return goalScore;
    }

    public void setGoalScore(int goalScore) {
        this.goalScore.set(goalScore);
    }

    public int getYellowCard() {
        return yellowCard.get();
    }

    public IntegerProperty yellowCardProperty() {
        return yellowCard;
    }

    public void setYellowCard(int yellowCard) {
        this.yellowCard.set(yellowCard);
    }

    public int getRedCard() {
        return redCard.get();
    }

    public IntegerProperty redCardProperty() {
        return redCard;
    }

    public void setRedCard(int redCard) {
        this.redCard.set(redCard);
    }
}
