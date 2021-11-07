package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
    private final int NO_CHILDREN_INT = -1;
    private ChildrenManager childrenManager;
    private List<Child> childrenList = new ArrayList<>();
    private List<String> childrenFullNames = new ArrayList<>();
    private CoinFlipManager coinFlipManager;
    GifImageView coinFlipAnimated;
    ImageView resultHead;
    ImageView resultTail;
    Boolean result;
    Boolean childPickedHead;
    int pickedChildIndex;
    int suggestedChildIndex;
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

        resultHead = findViewById(R.id.resultHead);
        resultHead.setVisibility(View.INVISIBLE);

        resultTail = findViewById(R.id.resultTail);
        resultTail.setVisibility(View.INVISIBLE);

        populateChildrenList();
        setSuggestedChildIndex();
        displaySuggestedChild();
        displayDropDownList();
        attachButtonListeners();
    }

    private void setSuggestedChildIndex() {
        int lastChildIndex = getSharedPrefLastChildIndex(this);

        if (childrenList.size() > 0) {
            if (lastChildIndex != NO_CHILDREN_INT && lastChildIndex < childrenList.size() - 1) {
                suggestedChildIndex = lastChildIndex + 1;
            } else {
                suggestedChildIndex = 0;
            }
        } else {
            suggestedChildIndex = NO_CHILDREN_INT;
        }
    }

    private int getSharedPrefLastChildIndex(Context context) {
        String lastPickedChildKey = context.getResources().getString(R.string.shared_pref_suggested_child_key);
        return Helpers.getSharedPreference(context).getInt(lastPickedChildKey, NO_CHILDREN_INT);
    }

    private void displaySuggestedChild() {
        TextView suggestedChildText = findViewById(R.id.suggestedChild);

        if (suggestedChildIndex != NO_CHILDREN_INT) {
            suggestedChildText.setText("You next suggested child is: " + childrenFullNames.get(suggestedChildIndex));

        } else {
            suggestedChildText.setText("No children in the list for suggestion!");
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
        for (int i = 0; i < childrenList.size(); i++) {
            Child childInstance = childrenList.get(i);
            String fullName = childInstance.getFirstName() + " " + childInstance.getLastName();
            childrenFullNames.add(fullName);
        }
    }

    private void displayDropDownList() {
        Spinner spinner = findViewById(R.id.childrenListPicked);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CoinFlipActivity.this,
                R.layout.support_simple_spinner_dropdown_item, childrenFullNames);
        spinner.setAdapter(adapter);
        spinner.setSelection(suggestedChildIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                pickedChildIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
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
        Button addTailBtn = findViewById(R.id.addTail);
        addHeadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTailBtn.setBackgroundColor(0x45A364);
                addHeadBtn.setBackgroundColor(0xFF17602F);
                childPickedHead = true;
            }
        });

        addTailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHeadBtn.setBackgroundColor(0x45A364);
                addTailBtn.setBackgroundColor(0xFF17602F);
                childPickedHead = false;
            }
        });

        Button coinFlipActivate = findViewById(R.id.coinFlipActivate);
        coinFlipActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultHead.setVisibility(View.INVISIBLE);
                resultTail.setVisibility(View.INVISIBLE);

                if (childPickedHead == null) {
                    Toast.makeText(getApplicationContext(), "Please select Head or Tail!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Helpers.getMediaPlayer(CoinFlipActivity.this, R.raw.coin_flip_sound).start();
                    coinFlipAnimated.setVisibility(View.VISIBLE);
                    result = rng.nextInt() % 2 == 0;

                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {}
                        public void onFinish() {
                            displayDialog();
                        }
                    }.start();
                }
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

    private void displayDialog() {
        coinFlipAnimated.setVisibility(View.INVISIBLE);
        String resultedSide;
        CoinFlip flip;

        if (suggestedChildIndex != NO_CHILDREN_INT) {
            Child pickedChild = childrenList.get(pickedChildIndex);
            flip = new CoinFlip(pickedChild, result, childPickedHead);
        } else {
            flip = new CoinFlip(new Child("Children:","No"), result, true);
            pickedChildIndex = NO_CHILDREN_INT;
        }

        if (result) {
            resultHead.setVisibility(View.VISIBLE);
            resultedSide = "Head";
        } else {
            resultTail.setVisibility(View.VISIBLE);
            resultedSide = "Tail";
        }

        String resultMessage = "Result: " + resultedSide + " !\n" + flip.getPickerStatus();
        FragmentManager manager = getSupportFragmentManager();
        FlipCoinMessageFragment dialog = new FlipCoinMessageFragment(result, resultMessage);
        dialog.show(manager, "MessageDialog");
        Log.i("TAG","Showed the dialog.");

        coinFlipManager.addCoinFlip(flip);
        updateSuggestedChildSharedPref(pickedChildIndex, this);
        updateCoinFlipHistorySharedPref();
    }

    private void updateCoinFlipHistorySharedPref() {
        Context ctx = getApplicationContext();
        String historyKey = ctx.getResources().getString(R.string.shared_pref_coin_flip_history_key);
        Helpers.saveObjectToSharedPreference(ctx, historyKey, coinFlipManager.getCoinFlipHistory());
    }

    private void updateSuggestedChildSharedPref(int savedIndex, Context context) {
        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(context);
        String lastPickedChildKey = context.getResources().getString(R.string.shared_pref_suggested_child_key);
        editor.putInt(lastPickedChildKey, savedIndex);
        editor.apply();
    }
}