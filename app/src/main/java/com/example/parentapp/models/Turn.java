package com.example.parentapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Turn class represents a turn object
 * Currently stores the index of the task, time of which child has confirmed their turn,
 * and the id of that child.
 */
public class Turn {

    private String taskID;
    private String formattedTurnTime;
    private String childID;

    public Turn(String taskID, String childID) {
        this.taskID = taskID;
        this.formattedTurnTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd @ h:mm a"));
        this.childID = childID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskIndex) {
        this.taskID = taskIndex;
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getTurnStatus(Child child) {
        String turnStatus = "";
        if (child == null) {
            turnStatus = "A deleted child" + "\nConfirmed turn on " + formattedTurnTime;
        } else {
            turnStatus = child.getFirstName() + " " + child.getLastName() + "\nConfirmed turn on " + formattedTurnTime;
        }

        return turnStatus;
    }
}
