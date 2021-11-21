package com.example.parentapp.models;

import android.graphics.Bitmap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The coinFlip model that represents a coin flip instance
 *
 * model records the child that was given the pick, as well as whether the child picked head/tail and won/lost
 */

public class CoinFlip {

    private String formattedCoinFlipTime;
    private String pickerID;
    private boolean pickerWon;
    private boolean pickedHead;
    private boolean flippedHead;

    public CoinFlip(String pickerID, boolean flippedHead, boolean pickedHead) {
        this.formattedCoinFlipTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd @ h:mm a"));
        this.pickerID = pickerID;
        this.pickedHead = pickedHead;
        this.flippedHead = flippedHead;
        pickerWon = flippedHead == pickedHead;
    }

    public CoinFlip(boolean flippedHead) {
        this.formattedCoinFlipTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd @ h:mm a"));
        this.flippedHead = flippedHead;
        this.pickerWon = true;
    }

    public String getFormattedCoinFlipTime() {
        return this.formattedCoinFlipTime;
    }

    public String getPickerID() {
        return pickerID;
    }

    public void setPicker(String pickerID) {
        this.pickerID = pickerID;
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

    public String getPickerStatus(Child picker) {
        String pickerStatus = "";
        if (picker == null) {
            pickerStatus = "Anonymous coin flip resulted in " + (this.flippedHead ? "head." : "tail.");
        } else {
            pickerStatus = picker.getFirstName() + " " + picker.getLastName() + " picked " + (this.pickedHead ? "head" : "tail") + " and " + (this.pickerWon ? "won" : "lost");
        }

        return pickerStatus;
    }
}
