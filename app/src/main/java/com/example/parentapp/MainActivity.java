package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The MainActivity class is an android activity that acts as a landing page.
 * Upon landing, users can then navigate to ChildrenActivity, CoinFlipActivity and TimerActivity
 */

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
        //TODO uncomment this
//        timerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = TimerActivity.makeIntent(MainActivity.this);
//                startActivity(intent);
//            }
//        });
        //TODO comment this
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HelpScreenActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }
}