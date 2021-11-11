package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HelpScreenActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context,HelpScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
    }
}