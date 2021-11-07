package com.example.parentapp.models;

import java.util.ArrayList;

public class CoinFlipManager {
    private static CoinFlipManager coinFlipManager;
    private ArrayList<CoinFlip> coinFlipHistory;

    public static CoinFlipManager getInstance() {
        if (coinFlipManager == null) {
            coinFlipManager = new CoinFlipManager();
        }

        return coinFlipManager;
    }

    private CoinFlipManager() {
        this.coinFlipHistory = new ArrayList<>();
    }

    public ArrayList<CoinFlip> getCoinFlipHistory() {
        return this.coinFlipHistory;
    }

    public void setCoinFlipHistory(ArrayList<CoinFlip> coinFlipHistory) {
        this.coinFlipHistory = coinFlipHistory;
    }

    public CoinFlip getCoinFlip(int coinFlipInstanceId) {
        if (coinFlipInstanceId >= this.coinFlipHistory.size()) {
            throw new IllegalArgumentException("Coin flip instance ID: " + coinFlipInstanceId + " exceeds coin flip count.");
        }

        if (this.coinFlipHistory.get(coinFlipInstanceId) == null) {
            throw new IllegalArgumentException("Coin flip instance ID: " + coinFlipInstanceId + " not found.");
        }

        return this.coinFlipHistory.get(coinFlipInstanceId);
    }

    public CoinFlip addCoinFlip(CoinFlip coinFlip) {
        this.coinFlipHistory.add(coinFlip);
        return coinFlip;
    }

    public CoinFlip updateCoinFlip(int index, CoinFlip coinFlip) {
        this.coinFlipHistory.set(index, coinFlip);
        return coinFlip;
    }

    public void removeCoinFlip(int coinFlipInstanceId) {
        if (coinFlipInstanceId >= this.coinFlipHistory.size()) {
            throw new IllegalArgumentException("Coin flip instance ID: " + coinFlipInstanceId + " exceeds coin flip count.");
        }

        if (this.coinFlipHistory.get(coinFlipInstanceId) == null) {
            throw new IllegalArgumentException("Coin flip instance ID: " + coinFlipInstanceId + " not found.");
        }

        this.coinFlipHistory.remove(coinFlipInstanceId);
    }

    public void resetCoinFlipHistory() {
        this.coinFlipHistory = new ArrayList<>();
    }
}
