package com.example.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.Helpers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        childLastName = findViewById(R.id.fillLastName);

        // Set up UI elements
        setUpAppBar();
        setUpSaveButton();
        setUpDeleteButton();
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
        String message;

        if (title.equals("New")) {
            message = "New child is added.";
            childrenManager.addChild(new Child(lastName, firstName));
        } else {
            message = "Child's information has been edited.";
            Child childEdited = new Child(lastName, firstName);
            childrenManager.updateChild(editIndex, childEdited);
        }

        Toast.makeText(ChildrenEditActivity.this, message, Toast.LENGTH_SHORT)
                .show();
        updateChildrenListSharedPref();
        finish();
    }

    private void setUpDeleteButton() {
        FloatingActionButton deleteButton = findViewById(R.id.childDeleteButton);

        if(title.equals("Edit")) {
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenEditActivity.this);
                builder.setMessage("Delete this child? It cannot be restored!")
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            childrenManager.removeChild(editIndex);
                            updateChildrenListSharedPref();
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

    private void fillNameFields() {
        if (title.equals("Edit")) {
            Child childInstance = childrenManager.getChild(editIndex);

            childFirstName = findViewById(R.id.fillFirstName);
            childFirstName.setText(childInstance.getFirstName());

            childLastName = findViewById(R.id.fillLastName);
            childLastName.setText(childInstance.getLastName());
        }
    }

    private void updateChildrenListSharedPref() {
        Context context = getApplicationContext();
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);
        Helpers.saveObjectToSharedPreference(context, childrenListKey, childrenManager.getChildren());
    }
}