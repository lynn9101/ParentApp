package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
        CardView childrenBtn = findViewById(R.id.childrenActivityButton);
        childrenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChildrenActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });

        CardView taskBtn = findViewById(R.id.taskActivityButton);
        taskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = WhoseTurnActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });


        CardView coinFlipBtn = findViewById(R.id.coinFlipActivityButton);
        coinFlipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CoinFlipActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });

        CardView timerBtn = findViewById(R.id.timerActivityButton);
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TimerActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });

        Button helpActivityBtn = findViewById(R.id.helpActivityButton);
        helpActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}