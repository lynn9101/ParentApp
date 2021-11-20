package com.example.parentapp.models;

import java.util.ArrayList;

/**
 * The TasksManager class is a singleton used throughout the app.
 * The class is responsible for the retrieval, addition, modification and deletion of task object.
 */

public class TasksManager {

    private static TasksManager tasksManager;
    private ArrayList<Task> tasksHistory;

    public static TasksManager getInstance() {
        if (tasksManager == null) {
            tasksManager = new TasksManager();
        }

        return tasksManager;
    }

    private TasksManager() {
        this.tasksHistory = new ArrayList<>();
    }

    public ArrayList<Task> getTasksHistory() {
        return tasksHistory;
    }

    public void setTasksHistory(ArrayList<Task> tasksHistory) {
        this.tasksHistory = tasksHistory;
    }

    public Task getTask(int taskIndex) {
        if (taskIndex >= this.tasksHistory.size()) {
            throw new IllegalArgumentException("Task Index: " + taskIndex + " exceeds tasks count.");
        }

        if (this.tasksHistory.get(taskIndex) == null) {
            throw new IllegalArgumentException("Task Index: " + taskIndex + " not found.");
        }

        return this.tasksHistory.get(taskIndex);
    }

    public void addTask(Task task) {
        this.tasksHistory.add(task);
    }

    public Task updateTask(int index, Task task) {
        this.tasksHistory.set(index, task);
        return task;
    }

    public void removeTask(int taskIndex) {
        if (taskIndex >= this.tasksHistory.size()) {
            throw new IllegalArgumentException("Task Index: " + taskIndex + " exceeds tasks count.");
        }

        if (this.tasksHistory.get(taskIndex) == null) {
            throw new IllegalArgumentException("Task Index: " + taskIndex + " not found.");
        }

        this.tasksHistory.remove(taskIndex);
    }
}
