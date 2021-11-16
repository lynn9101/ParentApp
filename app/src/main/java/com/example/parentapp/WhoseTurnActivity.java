package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.example.parentapp.models.Task;
import com.example.parentapp.models.TasksManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * WhoseTurnActivity class is an android activity and handles the listview of task objects
 */

public class WhoseTurnActivity extends AppCompatActivity {

    private TasksManager tasksManager = TasksManager.getInstance();
    private List<Task> tasksHistory = new ArrayList<>();

    public static Intent makeIntent(Context context) {
        return new Intent(context, WhoseTurnActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whose_turn);

        getSupportActionBar().setTitle(R.string.whose_turn_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        attachAddButtonListener();
        displayEmptyListState();
    }

    private void attachAddButtonListener() {
        FloatingActionButton addButton = findViewById(R.id.taskAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TaskEditActivity.makeLaunchIntent(WhoseTurnActivity.this, "New");
                startActivity(intent);
            }
        });
    }

    private void displayEmptyListState() {
    }

}