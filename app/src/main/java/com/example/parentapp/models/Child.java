package com.example.parentapp.models;

public class Child {
    private String lastName;
    private String firstName;
    private boolean gotPicked;

    public Child(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
        gotPicked = false;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean isGotPicked() {
        return gotPicked;
    }

    public void setGotPicked(boolean gotPicked) {
        this.gotPicked = gotPicked;
    }
}
