package com.example.parentapp.models;

/**
 * The Child class represents a child object
 * Children be enrolled into a queue that determines who get to flip a coin
 * Currently only the name of the child is stored
 */
public class Child {
    private String lastName;
    private String firstName;

    public Child(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
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
}
