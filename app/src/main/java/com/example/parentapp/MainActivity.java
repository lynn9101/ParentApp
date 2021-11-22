package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.parentapp.models.Helpers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        checkDataVersion();
        requestAppPermissions();
        attachButtonListeners();
    }

    private void checkDataVersion() {
        Context context = getApplicationContext();
        String versionDateKey = context.getResources().getResourceEntryName(R.string.version_date);
        String versionDate = context.getResources().getString(R.string.version_date);
        SharedPreferences sharedPref = Helpers.getSharedPreference(getApplicationContext());

        //clear existing data if version mismatch
        if (Helpers.isStringNullOrEmpty(versionDate) ||
                !sharedPref.contains(versionDateKey) ||
                !sharedPref.getString(versionDateKey, null).equals(versionDate)) {

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.putString(versionDateKey, versionDate);
            editor.commit();
        }
    }

    private void requestAppPermissions() {
        final int CAMERA_REQUEST_CODE = 0;

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
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

        FloatingActionButton helpActivityBtn = findViewById(R.id.helpActivityButton);
        helpActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = HelpScreenActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }
}