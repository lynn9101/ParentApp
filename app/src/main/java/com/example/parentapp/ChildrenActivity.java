package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChildrenActivity extends AppCompatActivity {
    private ChildrenManager childrenManager = ChildrenManager.getInstance();
    private List<Child> childrenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);

        getSupportActionBar().setTitle(R.string.children_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attachButtonListeners();
        displayEmptyList();
        populateChildrenList();
        registerClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.activity_children);

        getSupportActionBar().setTitle(R.string.children_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attachButtonListeners();
        displayEmptyList();
        populateChildrenList();
        registerClickListener();
    }

    private void attachButtonListeners() {
        FloatingActionButton fab = findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChildrenEditActivity.makeLaunchIntent(ChildrenActivity.this, "New");
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
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);

        if (sharedPreferences.contains(childrenListKey)) {
            childrenManager.setChildren(Helpers.getObjectFromSharedPreference(context, childrenListKey, Helpers.getListOfClassType(Child.class)));
        }

        this.childrenList = childrenManager.getChildren();
        ListView childrenListView = findViewById(R.id.listAllChildren);
        ArrayAdapter<Child> adapter = new ChildrenListAdapter();

        childrenListView.setAdapter(adapter);
    }

    private class ChildrenListAdapter extends ArrayAdapter<Child> {
        public ChildrenListAdapter() {
            super(ChildrenActivity.this, R.layout.children_list_view, childrenList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.children_list_view, parent, false);
            }

            Child childInstance = childrenList.get(position);

            ImageView childIcon = itemView.findViewById(R.id.childIcon);

            TextView childFirstName = itemView.findViewById(R.id.firstName);
            childFirstName.setText(childInstance.getFirstName());

            TextView childLastName = itemView.findViewById(R.id.lastName);
            childLastName.setText(childInstance.getLastName());

            return itemView;
        }
    }

    private void registerClickListener() {
        ListView list = findViewById(R.id.listAllChildren);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = ChildrenEditActivity.makeLaunchIntent(ChildrenActivity.this, "Edit");
                intent.putExtra("editIndex", position);
                startActivity(intent);
            }
        });
    }
}
