package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.Helpers;

public class ChildrenEditActivity extends AppCompatActivity {
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    private static final String EXTRA_MESSAGE = "Passing Edit State";
    private String title;
    private int editIndex;
    private boolean changesMade = false;

    private EditText childFirstName;
    private EditText childLastName;

    public static Intent makeLaunchIntent(Context ctx, String message) {
        Intent intent = new Intent(ctx, ChildrenEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_edit);

        // Get intent for adding vs. editing Child
        Intent intent = getIntent();
        title = intent.getStringExtra(EXTRA_MESSAGE);
        editIndex = intent.getIntExtra("editIndex", 0);

        childFirstName = findViewById(R.id.fillFirstName);
        
        // Set up UI elements
        setUpAppBar();
        setUpSaveButton();
        //setUpDeleteButton();
        fillNameFields();

        setUpSaveButton();
    }


    @Override
    protected void onStart() {
        super.onStart();
        changesMade = false;
    }

    private void setUpAppBar() {
        getSupportActionBar().setTitle(title + " Child");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.childSaveButton);
        saveButton.setOnClickListener(view -> {
            if (checkValidName()) {
                saveValidName();
            }
        });
    }

    private boolean checkValidName() {
        childFirstName = findViewById(R.id.fillFirstName);
        childLastName = findViewById(R.id.fillLastName);

        if (childFirstName.getText().toString().equals("") ||
            childLastName.getText().toString().equals(""))
        {
            String message = "Please fill out the full name!";
            Toast.makeText(ChildrenEditActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        return true;
    }

    private void saveValidName() {
        String firstName = childFirstName.getText().toString();
        String lastName = childLastName.getText().toString();

        childrenManager.addChild(new Child(firstName, lastName));
        String addMessage = "New child is added.";
        Toast.makeText(ChildrenEditActivity.this, addMessage, Toast.LENGTH_SHORT)
                .show();
        updateChildrenListSharedPref();
        finish();
    }

    private void fillNameFields() {

    }

    private void updateChildrenListSharedPref() {
        Context context = getApplicationContext();
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);
        Helpers.saveObjectToSharedPreference(context, childrenListKey, childrenManager.getChildren());
    }

}