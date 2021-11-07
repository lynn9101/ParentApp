package com.example.parentapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The coinFlip model that represents a coin flip instance
 *
 * model records the child that was given the pick, as well as whether the child picked head/tail and won/lost
 */

public class CoinFlip {

    private String formattedCoinFlipTime;
    private Child picker;
    private boolean pickerWon;
    private boolean pickedHead;
    private boolean flippedHead;

    public CoinFlip(Child picker, boolean flippedHead, boolean pickedHead) {
        this.formattedCoinFlipTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd @ h:mm a"));
        this.picker = picker;
        this.pickedHead = pickedHead;
        this.flippedHead = flippedHead;
        pickerWon = flippedHead == pickedHead;
    }

    public String getFormattedCoinFlipTime() {
        return this.formattedCoinFlipTime;
    }

    public Child getPicker() {
        return picker;
    }

    public void setPicker(Child picker) {
        this.picker = picker;
    }

    public boolean didPickerWin() {
        return pickerWon;
    }

    public void setPickerWon(boolean pickerWon) {
        this.pickerWon = pickerWon;
    }

    public boolean isPickedHead() {
        return pickedHead;
    }

    public void setPickedHead(boolean pickedHead) {
        this.pickedHead = pickedHead;
    }

    public String getPickerStatus() {
        String pickerStatus = "";
        if (picker.getFirstName().equals("No")) {
            pickerStatus = "No children to pick! Get " + (this.flippedHead ? "head" : "tail");
        } else {
            pickerStatus = picker.getFirstName() + " " + picker.getLastName() + " picked " + (this.pickedHead ? "head" : "tail") + " and " + (this.pickerWon ? "won" : "lost");
        }

        return pickerStatus;
    }
}
