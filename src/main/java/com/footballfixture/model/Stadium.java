package com.footballfixture.model;

import javafx.beans.property.*;

public class Stadium {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty city;
    private final IntegerProperty capacity;

    public Stadium(int id, String name, String city, int capacity) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.city = new SimpleStringProperty(city);
        this.capacity = new SimpleIntegerProperty(capacity);
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

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public int getCapacity() {
        return capacity.get();
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity.set(capacity);
    }
}
