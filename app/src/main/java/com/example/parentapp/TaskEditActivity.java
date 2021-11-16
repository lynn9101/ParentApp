package com.example.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentapp.models.TasksManager;

/**
 * TaskEditActivity class is an android activity which handles the addition, modification and
 * deletion of the task object
 */

public class TaskEditActivity extends AppCompatActivity {

    private TasksManager tasksManager = TasksManager.getInstance();
    private static final String EXTRA_MESSAGE = "Passing Edit State";
    private String title;
    private int editIndex;

    public static Intent makeLaunchIntent(Context ctx, String message) {
        Intent intent = new Intent(ctx, TaskEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
    }
}