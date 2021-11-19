package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.ArrayList;

/**
 * WhoseTurnActivity class is an android activity and handles the listview of task objects.
 * ListView display the tasks and the names of children assigned to those tasks.
 */

public class WhoseTurnActivity extends AppCompatActivity {

    private TasksManager tasksManager = TasksManager.getInstance();
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    private ArrayList<Task> tasksHistory = new ArrayList<>();

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

        // Set up UI elements
        attachAddButtonListener();
        updateChildrenList();
        populateTaskList();
        displayEmptyListState();
        registerClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_whose_turn);

        attachAddButtonListener();
        updateChildrenList();
        populateTaskList();
        displayEmptyListState();
        registerClickListener();
    }

    private void attachAddButtonListener() {
        FloatingActionButton addButton = findViewById(R.id.taskAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TaskEditActivity.makeLaunchIntent(WhoseTurnActivity.this, "Add New");
                startActivity(intent);
            }
        });
    }

    private void updateChildrenList() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);

        if (sharedPreferences.contains(childrenListKey)) {
            childrenManager.setChildren(Helpers.getObjectFromSharedPreference(context, childrenListKey, Helpers.getListOfClassType(Child.class)));
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

        adapter.notifyDataSetChanged();
        tasksListView.setAdapter(adapter);
    }

    private void displayEmptyListState() {
        TextView noTaskText = findViewById(R.id.noTaskText);
        ImageView addTaskIcon = findViewById(R.id.addTaskIcon);
        TextView addTaskInstruction = findViewById(R.id.addTaskInstruction);

        if (tasksHistory.size() != 0) {
            noTaskText.setVisibility(View.INVISIBLE);
            addTaskIcon.setVisibility(View.INVISIBLE);
            addTaskInstruction.setVisibility(View.INVISIBLE);
        }
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
            TextView childFullName = itemView.findViewById(R.id.currentChildName);
            ImageView currentChildIcon = itemView.findViewById(R.id.taskChildImage);

            final int DEFAULT_NO_CHILDREN_IDX = -1;
            if (childrenManager.getChildren().size() == 0) {
                // If all children in Configure Children are removed, update the task information
                // to no assigned child to all the task.
                if (currentChildIndex != DEFAULT_NO_CHILDREN_IDX) {
                    currentChildIndex = DEFAULT_NO_CHILDREN_IDX;
                    tasksManager.getTask(position).setCurrentChildIndex(currentChildIndex);
                    updateTasksListSharedPref();
                }

                String noChildText = "No assigned child";
                childFullName.setText(noChildText);
                currentChildIcon.setImageResource(R.drawable.task_no_child_icon);

            } else {
                // If there exists children in Configure Children and the current task is not assigned
                // to anyone, update the task to the FIRST child in the list (index 0)
                if (currentChildIndex == DEFAULT_NO_CHILDREN_IDX) {
                    currentChildIndex = 0;
                    tasksManager.getTask(position).setCurrentChildIndex(currentChildIndex);
                    updateTasksListSharedPref();
                }

                Child childInstance = childrenManager.getChild(currentChildIndex);
                String fullName = childInstance.getFirstName() + " " + childInstance.getLastName();
                childFullName.setText(fullName);
                if (childInstance.hasPortrait()) {
                    currentChildIcon.setImageBitmap(childInstance.getPortrait());
                }
            }

            return itemView;
        }
    }

    private void registerClickListener() {
        ListView tasksList = findViewById(R.id.tasksListView);
        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FragmentManager manager = getSupportFragmentManager();
                TaskMessageFragment dialog = new TaskMessageFragment(position);
                dialog.show(manager, "TaskMessageDialog");
                Log.i("TAG", "Showed task pop-up message.");
            }
        });
    }

    private void updateTasksListSharedPref() {
        Context context = getApplicationContext();
        String tasksListKey = context.getResources().getString(R.string.shared_pref_tasks_list_key);
        Helpers.saveObjectToSharedPreference(context, tasksListKey, tasksManager.getTasksHistory());
    }
}