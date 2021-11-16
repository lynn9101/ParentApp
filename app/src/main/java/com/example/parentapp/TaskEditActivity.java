package com.example.parentapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.Helpers;
import com.example.parentapp.models.Task;
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

    private EditText taskName;

    public static Intent makeLaunchIntent(Context ctx, String message) {
        Intent intent = new Intent(ctx, TaskEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // Get intent for adding vs editing Task
        Intent intent = getIntent();
        title = intent.getStringExtra(EXTRA_MESSAGE);
        editIndex = intent.getIntExtra("editIndex", -1);

        taskName = findViewById(R.id.editTaskName);

        // Set up UI elements
        setUpAppBar();
        setUpSaveButton();
        setUpDeleteButton();
        fillModel();
        setUpSaveButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUpAppBar() {
        getSupportActionBar().setTitle(title + " Task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));
    }

    private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.taskSaveButton);
        saveButton.setOnClickListener(view -> {
            if (checkValidTaskName()) {
                saveTaskName();
            }
        });
    }

    private boolean checkValidTaskName() {
        if (taskName.getText().toString().equals("")) {
            String message = "Please fill out the task name before saving!";
            Toast.makeText(TaskEditActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        return true;
    }

    private void saveTaskName() {
        String validTaskName = taskName.getText().toString();
        int firstChildIndex = 0;
        String message;

        if (title.equals("Add New")) {
            message = "New task is added.";
            tasksManager.addTask(new Task(validTaskName, firstChildIndex));
        } else {
            message = "Task's name has been edited.";
            int currentChildIndex = tasksManager.getTask(editIndex).getCurrentChildIndex();
            Task taskEdited = new Task(validTaskName, currentChildIndex);
            tasksManager.updateTask(editIndex, taskEdited);
        }

        Toast.makeText(TaskEditActivity.this, message, Toast.LENGTH_SHORT)
                .show();
        updateTasksListSharedPref();
        finish();
    }

    private void setUpDeleteButton() {
        Button deleteButton = findViewById(R.id.taskDeleteButton);

        if (title.equals("Edit Current")) {
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskEditActivity.this);
                builder.setMessage("Delete this task? It cannot be restored!")
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            tasksManager.removeTask(editIndex);
                            updateTasksListSharedPref();
                            finish();
                        })
                        .setNegativeButton("Back", null);

                AlertDialog warning = builder.create();
                warning.show();
            });
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void fillModel() {
        if (editIndex >= 0) {
            Task taskInstance = tasksManager.getTask(editIndex);

            taskName.setText(taskInstance.getTaskName());
        }
    }

    private void updateTasksListSharedPref() {
        Context context = getApplicationContext();
        String tasksListKey = context.getResources().getString(R.string.shared_pref_tasks_list_key);
        Helpers.saveObjectToSharedPreference(context, tasksListKey, tasksManager.getTasksHistory());
    }
}