package com.example.parentapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Turn class represents a turn object
 * Currently stores the unique ID of the task, time when child has confirmed their turn for that task,
 * and the unique ID of that child.
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
            turnStatus = "A deleted child" + "\nFinished turn on " + formattedTurnTime;
        } else {
            turnStatus = child.getFirstName() + " " + child.getLastName() + "\nFinished turn on " + formattedTurnTime;
        }

        return turnStatus;
    }
}
