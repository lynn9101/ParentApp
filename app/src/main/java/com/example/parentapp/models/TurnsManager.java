package com.example.parentapp.models;

import java.util.ArrayList;

/**
 * The TurnsManager class is a singleton used throughout the app
 * This class manages the retrieval & deletion of Turn instances
 * It also keeps a history of past turn instances
 */

public class TurnsManager {
    private static TurnsManager turnsManager;
    private ArrayList<Turn> turnsHistory;

    public static TurnsManager getInstance() {
        if (turnsManager == null) {
            turnsManager = new TurnsManager();
        }

        return turnsManager;
    }

    private TurnsManager() {
        this.turnsHistory = new ArrayList<>();
    }

    public ArrayList<Turn> getTurnsHistory() {
        return turnsHistory;
    }

    public void setTurnsHistory(ArrayList<Turn> turnsHistory) {
        this.turnsHistory = turnsHistory;
    }

    public ArrayList<Turn> getTurnsHistoryByTaskID(String taskUUID) {
        ArrayList<Turn> turnsListPerTask = new ArrayList<>();

        for (int i = 0; i < turnsHistory.size(); i++) {
            if (turnsHistory.get(i).getTaskID().equals(taskUUID)) {
                turnsListPerTask.add(turnsHistory.get(i));
            }
        }

        return turnsListPerTask;
    }


    public Turn getTurnPerTask(ArrayList<Turn> turnsList, int turnInstanceId) {
        if (turnInstanceId >= turnsList.size()) {
            throw new IllegalArgumentException("Turn instance ID: " + turnInstanceId + " exceeds turns count.");
        }

        if (turnsList.get(turnInstanceId) == null) {
            throw new IllegalArgumentException("Turn instance ID: " + turnInstanceId + " not found.");
        }

        return turnsList.get(turnInstanceId);
    }

    public Turn addTurn(Turn newTurn) {
        this.turnsHistory.add(newTurn);
        return newTurn;
    }

    public Turn updateTurn(int index, Turn newTurn) {
        this.turnsHistory.set(index, newTurn);
        return newTurn;
    }

    private void removeTurn(int turnInstanceId) {
        if (turnInstanceId >= this.turnsHistory.size()) {
            throw new IllegalArgumentException("Turn instance ID: " + turnInstanceId + " exceeds turns count.");
        }

        if (this.turnsHistory.get(turnInstanceId) == null) {
            throw new IllegalArgumentException("Turn instance ID: " + turnInstanceId + " not found.");
        }

        this.turnsHistory.remove(turnInstanceId);
    }

    public void removeTurnsByTaskID(String taskUUID) {
        for (int i = 0; i < turnsHistory.size(); i++) {
            if (turnsHistory.get(i).getTaskID().equals(taskUUID)) {
                this.turnsHistory.remove(i);
            }
        }
    }
}
