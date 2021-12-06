package com.example.parentapp.models;

import android.graphics.Bitmap;

/**
 * The Child class represents a child object
 * Children be enrolled into a queue that determines who get to flip a coin
 * Currently only the name of the child is stored
 */
public class Child {

    private String lastName;
    private String firstName;
    private String portraitBitMap;
    private String uniqueID;

    public Child(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Child(String lastName, String firstName, String portraitBitMap, String childID) {
        this(lastName, firstName);
        this.uniqueID = childID;
        this.portraitBitMap = portraitBitMap;
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

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int childID) {
        this.uniqueID = uniqueID;
    }

    public boolean hasPortrait() {
        return !Helpers.isStringNullOrEmpty(this.portraitBitMap);
    }

    public Bitmap getPortrait() {
        if (!Helpers.isStringNullOrEmpty(this.portraitBitMap)) {
            return Helpers.convertStringToBitmap(this.portraitBitMap);
        } else {
            return null;
        }
    }
}
