package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class HelpScreenActivity extends AppCompatActivity {
    private ToggleButton toggleButtonForMemOrCopy;
    private TextView textForTitle;
    private TextView textForMemOrCopy;

    public static Intent makeIntent(Context context) {
        return new Intent(context,HelpScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        getSupportActionBar().setTitle(R.string.help_screen_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        setInitialStates();

        toggleButtonForMemOrCopy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                attachButtonListener(isChecked);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setInitialStates(){
        toggleButtonForMemOrCopy  = findViewById(R.id.toggleMemOrCopy);
        textForTitle = findViewById(R.id.textTitleOfText);
        textForMemOrCopy = findViewById(R.id.textLongBox);

        toggleButtonForMemOrCopy.setText(getString(R.string.copyrightName));
        toggleButtonForMemOrCopy.setTextOff(getString(R.string.membersName));
        toggleButtonForMemOrCopy.setTextOn(getString(R.string.copyrightName));

        textForMemOrCopy.setMovementMethod(new ScrollingMovementMethod());
        textForMemOrCopy.setText(R.string.listOfMembers);
        textForTitle.setText(R.string.membersName);
        textForTitle.setAllCaps(true);
    }

    private void attachButtonListener(boolean isChecked){
        if (isChecked){
            textForTitle.setText(R.string.membersName);
            textForMemOrCopy.setText(R.string.listOfMembers);
        } else {
            textForTitle.setText(R.string.copyrightName);
            textForMemOrCopy.setText(R.string.help_text_box_content);
        }
    }
}