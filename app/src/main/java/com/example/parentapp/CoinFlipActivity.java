package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

/**
 * The CoinFlipActivity class is an android activity that handles child suggestion, coin flip generation, addition and coin flip history clearance
 */

public class CoinFlipActivity extends AppCompatActivity {
    private static final int NO_CHILDREN_INT = -1;
    private ChildrenManager childrenManager;
    private List<Child> childrenList = new ArrayList<>();
    private List<String> childrenFullNames = new ArrayList<>();
    private CoinFlipManager coinFlipManager;
    private GifImageView coinFlipAnimated;
    private MediaPlayer coinFlipSound;
    private Button addHeadBtn;
    private Button addTailBtn;
    private Button coinFlipActivate;
    private ImageView resultHead;
    private ImageView resultTail;
    private TextView pickStatus;
    private Boolean result;
    private Boolean childPickedHead;
    int suggestedChildIndex;
    private Random rng;

    public static Intent makeIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        getSupportActionBar().setTitle(R.string.coin_flip_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        this.childrenManager = ChildrenManager.getInstance();
        this.coinFlipManager = CoinFlipManager.getInstance();
        this.rng = new Random();

        if (coinFlipSound == null) {
            coinFlipSound = Helpers.getMediaPlayer(CoinFlipActivity.this, R.raw.coin_flip_sound);
        }
        coinFlipAnimated = findViewById(R.id.coinAnimation);
        coinFlipAnimated.setVisibility(View.INVISIBLE);

        resultHead = findViewById(R.id.resultHead);
        resultHead.setVisibility(View.INVISIBLE);

        resultTail = findViewById(R.id.resultTail);
        resultTail.setVisibility(View.INVISIBLE);

        populateChildrenList();
        setSuggestedChildIndex();
        displaySuggestedChildAndOptions();
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

    public static int getSharedPrefLastChildIndex(Context context) {
        String lastPickedChildKey = context.getResources().getString(R.string.shared_pref_suggested_child_key);
        return Helpers.getSharedPreference(context).getInt(lastPickedChildKey, NO_CHILDREN_INT);
    }

    private void displaySuggestedChildAndOptions() {
        TextView suggestedChildText = findViewById(R.id.suggestedChild);

        if (suggestedChildIndex != NO_CHILDREN_INT) {
            suggestedChildText.setText("You current suggested child is: " + childrenFullNames.get(suggestedChildIndex));

        } else {
            suggestedChildText.setText("No children in the list for suggestion!");
        }

        pickStatus = findViewById(R.id.pickStatus);
        pickStatus.setVisibility(View.VISIBLE);
        if (childrenList.size() == 0) {
            pickStatus.setVisibility(View.INVISIBLE);
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

    private void attachButtonListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoinFlipActivity.this, CoinFlipHistoryActivity.class);
                startActivity(intent);
            }
        });

        addHeadBtn = findViewById(R.id.addHead);
        addTailBtn = findViewById(R.id.addTail);
        if (childrenList.size() == 0) {
            addHeadBtn.setVisibility(View.INVISIBLE);
            addTailBtn.setVisibility(View.INVISIBLE);
        }

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

        coinFlipActivate = findViewById(R.id.coinFlipActivate);
        coinFlipActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = rng.nextInt() % 2 == 0;
                resultHead.setVisibility(View.INVISIBLE);
                resultTail.setVisibility(View.INVISIBLE);

                if (childrenList.size() != 0 && childPickedHead == null) {
                    Toast.makeText(getApplicationContext(), "Please select Head or Tail!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    addHeadBtn.setVisibility(View.INVISIBLE);
                    addTailBtn.setVisibility(View.INVISIBLE);
                    pickStatus.setVisibility(View.INVISIBLE);

                    if (coinFlipSound == null) {
                        coinFlipSound = Helpers.getMediaPlayer(CoinFlipActivity.this, R.raw.coin_flip_sound);
                    }
                    coinFlipSound.start();

                    coinFlipAnimated.setVisibility(View.VISIBLE);
                    coinFlipActivate.setVisibility(View.INVISIBLE);

                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            if (coinFlipAnimated != null && coinFlipSound != null) {
                                displayDialog();
                            }
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
        if (coinFlipAnimated != null) {
            coinFlipAnimated.setVisibility(View.INVISIBLE);
        }
        String resultedSide;
        CoinFlip flip;

        if (suggestedChildIndex != NO_CHILDREN_INT) {
            Child pickedChild = childrenList.get(suggestedChildIndex);
            flip = new CoinFlip(pickedChild, result, childPickedHead);
        } else {
            flip = new CoinFlip(result);
            suggestedChildIndex = NO_CHILDREN_INT;
        }

        if (result) {
            resultHead.setVisibility(View.VISIBLE);
            resultedSide = "Head";
        } else {
            resultTail.setVisibility(View.VISIBLE);
            resultedSide = "Tail";
        }

        coinFlipManager.addCoinFlip(flip);
        updateSuggestedChildSharedPref(suggestedChildIndex, this);
        updateCoinFlipHistorySharedPref();

        String resultMessage = "Result: " + resultedSide + " !\n" + flip.getPickerStatus();
        FragmentManager manager = getSupportFragmentManager();
        FlipCoinMessageFragment dialog = new FlipCoinMessageFragment(result, resultMessage);
        dialog.show(manager, "MessageDialog");
        Log.i("TAG", "Showed the dialog.");

        if (childrenList.size() != 0) {
            addHeadBtn.setVisibility(View.VISIBLE);
            addTailBtn.setVisibility(View.VISIBLE);
            pickStatus.setVisibility(View.VISIBLE);
        }
        coinFlipActivate.setVisibility(View.VISIBLE);
        coinFlipAnimated.setVisibility(View.INVISIBLE);
    }

    private void updateCoinFlipHistorySharedPref() {
        Context ctx = getApplicationContext();
        String historyKey = ctx.getResources().getString(R.string.shared_pref_coin_flip_history_key);
        Helpers.saveObjectToSharedPreference(ctx, historyKey, coinFlipManager.getCoinFlipHistory());
    }

    public static void updateSuggestedChildSharedPref(int savedIndex, Context context) {
        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(context);
        String lastPickedChildKey = context.getResources().getString(R.string.shared_pref_suggested_child_key);
        editor.putInt(lastPickedChildKey, savedIndex);
        editor.apply();
    }

    @Override
    protected void onStop() {
        if (coinFlipSound != null) {
            coinFlipSound.stop();
            coinFlipSound.release();
            coinFlipSound = null;
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (coinFlipSound != null) {
            coinFlipSound.stop();
            coinFlipSound.release();
            coinFlipSound = null;
        }

        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (coinFlipSound == null) {
            coinFlipSound = Helpers.getMediaPlayer(CoinFlipActivity.this, R.raw.coin_flip_sound);
        }
        coinFlipAnimated = findViewById(R.id.coinAnimation);
        coinFlipAnimated.setVisibility(View.INVISIBLE);
        coinFlipActivate.setVisibility(View.VISIBLE);

        resultHead = findViewById(R.id.resultHead);
        resultHead.setVisibility(View.INVISIBLE);

        resultTail = findViewById(R.id.resultTail);
        resultTail.setVisibility(View.INVISIBLE);

        setSuggestedChildIndex();
        displaySuggestedChildAndOptions();
        attachButtonListeners();
    }
}