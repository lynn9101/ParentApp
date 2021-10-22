package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChildrenEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_edit);

        getSupportActionBar().setTitle(R.string.children_edit_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}