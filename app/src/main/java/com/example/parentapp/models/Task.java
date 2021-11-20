package com.example.parentapp.models;

/**
 * The Task class represent a task object.
 * The name of the task and the index of current child responsible for that task are stored.
 */

public class Task {

    private String taskName;
    private int currentChildIndex;

    public Task(String taskName, int currentChildIndex) {
        this.taskName = taskName;
        this.currentChildIndex = currentChildIndex;
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