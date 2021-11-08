package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        attachButtonListeners();
    }

    private void attachButtonListeners() {
        Button childrenBtn = findViewById(R.id.childrenBtn);
        childrenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChildrenActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });

        Button coinFlipBtn = findViewById(R.id.coinFlipBtn);
        coinFlipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CoinFlipActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });

        Button timerBtn = findViewById(R.id.timerBtn);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TimerActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }
}