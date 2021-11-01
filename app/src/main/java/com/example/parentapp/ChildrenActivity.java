package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ChildrenActivity extends AppCompatActivity {
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    ArrayList<Child> childrenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);

        getSupportActionBar().setTitle(R.string.children_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attachButtonListeners();
        displayEmptyList();
        //populateChildrenList();
        //registerClickListener();
    }

    private void attachButtonListeners() {
        FloatingActionButton fab = findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildrenActivity.this, ChildrenEditActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayEmptyList() {
        TextView noChildText = findViewById(R.id.noChildText);
        ImageView addIcon = findViewById(R.id.addIcon);
        TextView addInstruction = findViewById(R.id.addInstruction);

        childrenList = childrenManager.getChildren();
        if(childrenList.size() != 0) {
            noChildText.setVisibility(View.INVISIBLE);
            addIcon.setVisibility(View.INVISIBLE);
            addInstruction.setVisibility(View.INVISIBLE);
        }
    }

    private void populateChildrenList() {
        // Create a list of children
        String[] strChildrenList = {"bryan", "lynn"};

        // Build an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.children_list_view, strChildrenList);

        // Configure the list view of children
        ListView list = findViewById(R.id.listAllChildren);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void registerClickListener() {
        ListView list = findViewById(R.id.listAllChildren);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = new Intent(ChildrenActivity.this, ChildrenEditActivity.class);
                startActivity(intent);
            }
        });
    }
}