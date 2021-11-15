package com.example.parentapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * The Child class represents a child object
 * Children be enrolled into a queue that determines who get to flip a coin
 * Currently only the name of the child is stored
 */
public class Child {

    private String lastName;
    private String firstName;
    private String portraitBitMap;
    //ignore property during serialization & deserialization
    private transient Bitmap portrait;

    public Child(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Child(String lastName, String firstName, Bitmap childPortrait) {
        this(lastName, firstName);
        this.setPortrait(childPortrait);
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

    public void setPortrait(Bitmap childPortrait) {
        this.portrait = childPortrait;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        childPortrait.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte [] byteArray = stream.toByteArray();
        this.portraitBitMap = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public boolean hasPortrait() {
        return this.portrait != null || !Helpers.isStringNullOrEmpty(this.portraitBitMap);
    }

    public Bitmap getPortrait() {
        if (this.portrait != null) {
            return this.portrait;
        } else if (!Helpers.isStringNullOrEmpty(this.portraitBitMap)) {
            byte [] byteArray = Base64.decode(this.portraitBitMap, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }
}
