package com.example.parentapp.models;

public class CoinFlipManager {
    private static CoinFlipManager coinFlipManager;

    public static CoinFlipManager getInstance() {
        if (coinFlipManager == null) {
            coinFlipManager = new CoinFlipManager();
        }

        return coinFlipManager;
    }

    private CoinFlipManager() {

    }
}
