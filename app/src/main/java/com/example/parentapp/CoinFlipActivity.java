package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.CoinFlip;
import com.example.parentapp.models.CoinFlipManager;
import com.example.parentapp.models.Helpers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class CoinFlipActivity extends AppCompatActivity {
    private ChildrenManager childrenManager;
    private List<Child> childrenList = new ArrayList<>();
    private List<String> childrenFullNames = new ArrayList<>();
    private CoinFlipManager coinFlipManager;
    GifImageView coinFlipAnimated;
    private Random rng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        getSupportActionBar().setTitle(R.string.coin_flip_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.childrenManager = ChildrenManager.getInstance();
        this.coinFlipManager = CoinFlipManager.getInstance();
        this.rng = new Random();

        coinFlipAnimated = findViewById(R.id.coinAnimation);
        coinFlipAnimated.setVisibility(View.INVISIBLE);

        populateChildrenList();
        displayDropDownList();
        attachButtonListeners();
    }

    private void populateChildrenList() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);

        if (sharedPreferences.contains(childrenListKey)) {
            childrenManager.setChildren(Helpers.getObjectFromSharedPreference(context, childrenListKey, Helpers.getListOfClassType(Child.class)));
        }

        this.childrenList = childrenManager.getChildren();
        for (int i = 0; i < childrenList.size(); i++) {
            Child childInInstance = childrenList.get(i);
            String fullName = childInInstance.getFirstName() + " " + childInInstance.getLastName();
            childrenFullNames.add(fullName);
        }
    }

    private void displayDropDownList() {
        Spinner spinner = findViewById(R.id.childrenListPicked);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CoinFlipActivity.this,
                R.layout.support_simple_spinner_dropdown_item, childrenFullNames);
        spinner.setAdapter(adapter);
    }


    private void attachButtonListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoinFlipActivity.this, CoinFlipHistoryActivity.class);
                startActivity(intent);
            }
        });

        Button addHeadBtn = findViewById(R.id.addHead);

        addHeadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = rng.nextInt() % 2 == 0;
                CoinFlip flip = new CoinFlip(new Child("a", "b"), result, true);
                coinFlipManager.addCoinFlip(flip);
                updateCoinFlipHistorySharedPref();
            }
        });

        Button addTailBtn = findViewById(R.id.addTail);

        addTailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = rng.nextInt() % 2 == 0;
                CoinFlip flip = new CoinFlip(new Child("a", "b"), result, false);
                coinFlipManager.addCoinFlip(flip);
                updateCoinFlipHistorySharedPref();
            }
        });

        Button coinFlipActivate = findViewById(R.id.coinFlipActivate);

        coinFlipActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinFlipAnimated.setVisibility(View.VISIBLE);

                new CountDownTimer(5000, 1000) {
                    public void onTick(long millisUntilFinished) {}
                    public  void onFinish() {
                        coinFlipAnimated.setVisibility(View.INVISIBLE);
                    }
                }.start();
            }
        });

        Button clearBtn = findViewById(R.id.clearCoinFlipHistory);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinFlipManager.resetCoinFlipHistory();
                updateCoinFlipHistorySharedPref();
            }
        });

    }

    private void updateCoinFlipHistorySharedPref() {
        Context ctx = getApplicationContext();
        String historyKey = ctx.getResources().getString(R.string.shared_pref_coin_flip_history_key);
        Helpers.saveObjectToSharedPreference(ctx, historyKey, coinFlipManager.getCoinFlipHistory());
    }
}