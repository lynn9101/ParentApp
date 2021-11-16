package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.Helpers;
import com.example.parentapp.models.Task;
import com.example.parentapp.models.TasksManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * WhoseTurnActivity class is an android activity and handles the listview of task objects
 */

public class WhoseTurnActivity extends AppCompatActivity {

    private TasksManager tasksManager = TasksManager.getInstance();
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
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
        populateTaskList();
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
        TextView noTaskText = findViewById(R.id.noTaskText);
        ImageView addTaskIcon = findViewById(R.id.addTaskIcon);
        TextView addTaskInstruction = findViewById(R.id.addTaskInstruction);

        tasksHistory = tasksManager.getTasksHistory();
        if (tasksHistory.size() != 0) {
            noTaskText.setVisibility(View.INVISIBLE);
            addTaskIcon.setVisibility(View.INVISIBLE);
            addTaskInstruction.setVisibility(View.INVISIBLE);
        }
    }

    private void populateTaskList() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        String tasksListKey = context.getResources().getString(R.string.shared_pref_tasks_list_key);

        if (sharedPreferences.contains(tasksListKey)) {
            tasksManager.setTasksHistory(Helpers.getObjectFromSharedPreference(context, tasksListKey, Helpers.getListOfClassType(Task.class)));
        }

        this.tasksHistory = tasksManager.getTasksHistory();
        ListView tasksListView = findViewById(R.id.tasksListView);
        ArrayAdapter<Task> adapter = new TasksListAdapter();

        tasksListView.setAdapter(adapter);
    }

    private class TasksListAdapter extends ArrayAdapter<Task> {
        public TasksListAdapter() {
            super(WhoseTurnActivity.this, R.layout.task_list_view, tasksHistory);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.task_list_view, parent, false);
            }

            Task taskInstance = tasksHistory.get(position);

            String taskName = taskInstance.getTaskName();
            TextView txtTaskName = itemView.findViewById(R.id.taskName);
            txtTaskName.setText(taskName);

            int currentChildIndex = taskInstance.getCurrentChildIndex();
            Child childInstance = childrenManager.getChild(currentChildIndex);

            String fullName = childInstance.getFirstName() + " " + childInstance.getLastName();
            TextView childFullName = itemView.findViewById(R.id.currentChildName);
            childFullName.setText(fullName);

            if (childInstance.hasPortrait()) {
                ImageView currentChildIcon = itemView.findViewById(R.id.taskChildImage);
                currentChildIcon.setImageBitmap(childInstance.getPortrait());
            }

            return itemView;
        }
    }


}