package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.Helpers;

public class ChildrenEditActivity extends AppCompatActivity {
    private ChildrenManager childrenManager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_edit);

        getSupportActionBar().setTitle(R.string.children_edit_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateChildrenListSharedPref() {
        Context context = getApplicationContext();
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);
        Helpers.saveObjectToSharedPreference(context, childrenListKey, childrenManager.getChildren());
    }

}