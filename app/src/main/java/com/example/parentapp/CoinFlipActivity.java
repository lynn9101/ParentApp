package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.CoinFlip;
import com.example.parentapp.models.CoinFlipManager;
import com.example.parentapp.models.Helpers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity {

    private CoinFlipManager coinFlipManager;
    private Random rng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        getSupportActionBar().setTitle(R.string.coin_flip_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.coinFlipManager = CoinFlipManager.getInstance();
        this.rng = new Random();

        attachButtonListeners();
    }

    private void attachButtonListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoinFlipActivity.this, CoinFlipHistoryActivity.class);
                startActivity(intent);
            }
        });

        Button addHeadBtn = findViewById(R.id.addHead);

        addHeadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = rng.nextInt() % 2 == 0;
                CoinFlip flip = new CoinFlip(new Child("a", "b"), result, true);
                coinFlipManager.addCoinFlip(flip);
                updateCoinFlipHistorySharedPref();
            }
        });

        Button addTailBtn = findViewById(R.id.addTail);

        addTailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = rng.nextInt() % 2 == 0;
                CoinFlip flip = new CoinFlip(new Child("a", "b"), result, false);
                coinFlipManager.addCoinFlip(flip);
                updateCoinFlipHistorySharedPref();
            }
        });

        Button clearBtn = findViewById(R.id.clearCoinFlipHistory);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinFlipManager.resetCoinFlipHistory();
                updateCoinFlipHistorySharedPref();
            }
        });

    }

    private void updateCoinFlipHistorySharedPref() {
        Context ctx = getApplicationContext();
        String historyKey = ctx.getResources().getString(R.string.shared_pref_coin_flip_history_key);
        Helpers.saveObjectToSharedPreference(ctx, historyKey, coinFlipManager.getCoinFlipHistory());
    }
}