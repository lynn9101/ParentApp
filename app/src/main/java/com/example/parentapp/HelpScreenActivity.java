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

        TextView textForLongTxt = findViewById(R.id.textLongBox);
        TextView textForTitle = findViewById(R.id.textTitleOfText);

        ToggleButton toggleButtonForMemOrCopy  = findViewById(R.id.toggleMemOrCopy);
        toggleButtonForMemOrCopy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    textForTitle.setText(R.string.membersName);
                    textForLongTxt.setText(R.string.listOfMembers);
                } else {
                    textForTitle.setText(R.string.copyrightName);
                    textForLongTxt.setText(R.string.very_long_text);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setInitialStates(){
        ToggleButton toggleButtonForMemOrCopy  = findViewById(R.id.toggleMemOrCopy);
        toggleButtonForMemOrCopy.setText(getString(R.string.copyrightName));
        toggleButtonForMemOrCopy.setTextOff(getString(R.string.membersName));
        toggleButtonForMemOrCopy.setTextOn(getString(R.string.copyrightName));

        TextView textForLongTxt = findViewById(R.id.textLongBox);
        TextView textForTitle = findViewById(R.id.textTitleOfText);

        textForLongTxt.setMovementMethod(new ScrollingMovementMethod());
        textForLongTxt.setText(R.string.listOfMembers);
        textForTitle.setText(R.string.membersName);
        textForTitle.setAllCaps(true);
    }
}