package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.TurnsManager;

public class WhoseTurnHistoryActivity extends AppCompatActivity {
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    private TurnsManager turnsManager = TurnsManager.getInstance();
    private static final String EXTRA_MESSAGE = "Passing Turns State";
    private String taskID;

    public static Intent makeIntent(Context context, String taskIDMessage) {
        Intent intent = new Intent(context, WhoseTurnHistoryActivity.class);
        intent.putExtra(EXTRA_MESSAGE, taskIDMessage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whose_turn_history);

        getSupportActionBar().setTitle(R.string.whose_turn_history_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));
    }
}