package com.example.parentapp.models;

/**
 * The Task class represent a task object.
 * The generated unique ID, name of the task and the index of current child responsible for that task are stored.
 */

public class Task {

    private String uniqueID;
    private String taskName;
    private int currentChildIndex;

    public Task(String taskID, String taskName, int currentChildIndex) {
        this.uniqueID = taskID;
        this.taskName = taskName;
        this.currentChildIndex = currentChildIndex;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getCurrentChildIndex() {
        return currentChildIndex;
    }

    public void setCurrentChildIndex(int currentChildIndex) {
        this.currentChildIndex = currentChildIndex;
    }
}