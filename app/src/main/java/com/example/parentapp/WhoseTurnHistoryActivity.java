package com.example.parentapp;

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
import com.example.parentapp.models.Turn;
import com.example.parentapp.models.TurnsManager;

import java.util.ArrayList;

/**
 * WhoseTurnHistoryActivity class is an android activity and handles the listview of turn objects.
 * ListView display the full history of turn per task: the names of children and date, time they had
 * confirmed their turn for that task.
 */

public class WhoseTurnHistoryActivity extends AppCompatActivity {

    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    private TurnsManager turnsManager = TurnsManager.getInstance();
    private static final String EXTRA_MESSAGE = "Passing Turns History State";
    private String taskUUID;
    private ArrayList<Turn> turnsHistoryPerTask = new ArrayList<>();

    public static Intent makeIntent(Context context, String taskIDMessage) {
        Intent intent = new Intent(context, WhoseTurnHistoryActivity.class);
        intent.putExtra(EXTRA_MESSAGE, taskIDMessage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whose_turn_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        // Get intent to edit the ActionBar's Title for each task
        Intent intent = getIntent();
        taskUUID = intent.getStringExtra(EXTRA_MESSAGE);
        String title = intent.getStringExtra("turnsHistory");
        getSupportActionBar().setTitle("History: " + title);
        
        // Set up UI elements
        populateTurnsListPerTask();
        displayEmptyState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_whose_turn_history);

        populateTurnsListPerTask();
        displayEmptyState();
    }

    private void populateTurnsListPerTask() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        String turnsListKey = context.getResources().getString(R.string.shared_pref_turns_list_key);

        if (sharedPreferences.contains(turnsListKey)) {
            turnsManager.setTurnsHistory(Helpers.getObjectFromSharedPreference(context, turnsListKey, Helpers.getListOfClassType(Turn.class)));
        }

        this.turnsHistoryPerTask = turnsManager.getTurnsHistoryByTaskID(taskUUID);
        ListView turnsListView = findViewById(R.id.turnsListView);
        ArrayAdapter<Turn> adapter = new TurnsListAdapter();

        adapter.notifyDataSetChanged();
        turnsListView.setAdapter(adapter);
    }

    private void displayEmptyState() {
        TextView noTurnText = findViewById(R.id.noTurnsRecordText);
        if (turnsHistoryPerTask.size() != 0) {
            noTurnText.setVisibility(View.INVISIBLE);
        }
    }

    private class TurnsListAdapter extends ArrayAdapter<Turn> {
        public TurnsListAdapter() {
            super(WhoseTurnHistoryActivity.this, R.layout.turn_list_view, turnsHistoryPerTask);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.turn_list_view, parent, false);
            }

            Turn turnInstance = turnsHistoryPerTask.get(position);

            ImageView turnChildPortrait = itemView.findViewById(R.id.turnChildPortrait);
            Child childTurn = childrenManager.getChildren().stream().filter(x -> x.getUniqueID().equals(turnInstance.getChildID())).findFirst().orElse(null);
            if (childTurn != null) {
                turnChildPortrait.setImageBitmap(childTurn.getPortrait());
            } else {
                turnChildPortrait.setImageDrawable(getResources().getDrawable(R.drawable.task_no_child_icon));
            }

            TextView turnStatus = itemView.findViewById(R.id.turnStatus);
            turnStatus.setText(turnInstance.getTurnStatus(childTurn));

            return itemView;
        }
    }
}