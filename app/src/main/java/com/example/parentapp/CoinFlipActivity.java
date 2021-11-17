package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.parentapp.spinner.SpinnerChildrenAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    private Spinner spinnerChildren;
    int lastSelectedChild;
    String childrenListKey = "ChildrenListKey";
    String childrenIDKey = "AllChildrenIDListKey";
    String spinnerChildrenKey = "SpinnerChildrenListKey";
    private TextView chooseHint;
    private SpinnerChildrenAdapter spinnerAdapter;
    private boolean flipped;
    private boolean isFinishFlip;
    private ArrayList<Integer> allChildrenID;

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
        spinnerChildren = findViewById(R.id.spinnerChildrenList);

        coinFlipAnimated = findViewById(R.id.coinAnimation);
        coinFlipAnimated.setVisibility(View.INVISIBLE);

        resultHead = findViewById(R.id.resultHead);
        resultHead.setVisibility(View.INVISIBLE);

        resultTail = findViewById(R.id.resultTail);
        resultTail.setVisibility(View.INVISIBLE);

        populateChildrenList();



        attachButtonListeners();
    }

    private void spinnerViewBegin() {
        spinnerAdapter = new SpinnerChildrenAdapter(CoinFlipActivity.this,childrenList);
        spinnerChildren.setAdapter(spinnerAdapter);
        spinnerChildren.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spinnerAdapter.notifyDataSetChanged();
                lastSelectedChild = position;
                resultHead.setVisibility(View.INVISIBLE);
                resultTail.setVisibility(View.INVISIBLE);
                Child clickedItem = (Child) adapterView.getItemAtPosition(position);
                String clickedChildName = clickedItem.getFirstName() + " " + clickedItem.getLastName();
                //Toast.makeText(CoinFlipActivity.this,clickedChildName + " is selected", Toast.LENGTH_SHORT).show();
                pickStatus = findViewById(R.id.pickStatus);
                pickStatus.setText(clickedChildName + " wants: ");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private int generateChildID() {
        Random rand = new Random();
        int newChildID = rand.nextInt(100000);
        while (allChildrenID.contains(newChildID)) {
            newChildID = rand.nextInt(100000);
        }
        allChildrenID.add(newChildID);
        return newChildID;
    }

    public void setArrayPrefs(ArrayList<Child> array, Context mContext) {
        SharedPreferences prefs = Helpers.getSharedPreference(mContext);
        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(getApplicationContext());
        Gson gson = new Gson();
        String json = gson.toJson(array);
        //childrenListKey= mContext.getResources().getString(R.string.shared_pref_children_list_key);
        editor.putString(spinnerChildrenKey, json);
        editor.putBoolean("ISFLIPPED", flipped);
        editor.putBoolean("ISFINISHED", isFinishFlip);
        editor.commit();
    }

    public ArrayList<Child> getArrayPrefs(Context mContext) {
        SharedPreferences prefs = Helpers.getSharedPreference(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(spinnerChildrenKey, "");
        Type type = new TypeToken<ArrayList<Child>>(){}.getType();
        ArrayList<Child> allChildren = gson.fromJson(json, type);
        return allChildren;
    }

    public static int getSharedPrefLastChildIndex(Context context) {
        String lastPickedChildKey = context.getResources().getString(R.string.shared_pref_suggested_child_key);
        return Helpers.getSharedPreference(context).getInt(lastPickedChildKey, NO_CHILDREN_INT);
    }

    private void populateChildrenList() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = Helpers.getSharedPreference(context);
        //String childrenListKey = context.getResources().getString(R.string.shared_pref_children_list_key);

        if (sharedPreferences.contains(spinnerChildrenKey)) {
            childrenManager.setSpinnerChildren(Helpers.getObjectFromSharedPreference(context, spinnerChildrenKey, Helpers.getListOfClassType(Child.class)));
        }
        lastSelectedChild = getSharedPrefLastChildIndex(this);
        flipped = Helpers.getSharedPreference(this).getBoolean("ISFLIPPED", false);
        isFinishFlip = Helpers.getSharedPreference(this).getBoolean("ISFINISHED", false);
        if (sharedPreferences.contains(childrenIDKey)) {
            allChildrenID = getAllChildrenID(this);
        }
        //allChildrenID = getAllChildrenID(this);
        if (lastSelectedChild >= childrenManager.getSpinnerChildren().size()) {
            lastSelectedChild = childrenManager.getSpinnerChildren().size() - 1;
        }
        //Toast.makeText(CoinFlipActivity.this,"lastSelectedChild is " + lastSelectedChild, Toast.LENGTH_SHORT).show();
        childrenList = childrenManager.getSpinnerChildren();
        //childrenList.clear();
        //childrenList = getArrayPrefs(this);
        Child nobody;
        int anonymousChildID = generateChildID();
        if (childrenList == null || childrenList.size() == 0) {
            Bitmap icon = ((BitmapDrawable)getResources().getDrawable(R.drawable.child_image_listview)).getBitmap();
            nobody = new Child("Child","Anonymous", icon, anonymousChildID);
        } else {
            Toast.makeText(CoinFlipActivity.this, "child list not 0", Toast.LENGTH_SHORT).show();
            if (flipped && isFinishFlip) {
                Child lastPickedChild = childrenList.get(lastSelectedChild);
                childrenList.remove(lastPickedChild);
                childrenList.add(lastPickedChild);
                lastSelectedChild += 1;
                //Toast.makeText(CoinFlipActivity.this, "flipped", Toast.LENGTH_SHORT).show();
                flipped = false;
                isFinishFlip = false;
            }
            Bitmap icon = ((BitmapDrawable)getResources().getDrawable(R.drawable.child_image_listview)).getBitmap();
            nobody = new Child("Child","Anonymous", icon, anonymousChildID);
        }
        childrenList.add(nobody);
        for (int i = 0; i < childrenList.size(); i++) {
            Child childInstance = childrenList.get(i);
            String fullName = childInstance.getFirstName() + " " + childInstance.getLastName();
            childrenFullNames.add(fullName);
        }
        spinnerViewBegin();
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
                    flipped = true;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    coinFlipAnimated.setVisibility(View.VISIBLE);
                    coinFlipActivate.setVisibility(View.INVISIBLE);
                    spinnerChildren.setClickable(false);
                    chooseHint = findViewById(R.id.suggestedChild);
                    chooseHint.setVisibility(View.INVISIBLE);
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
        isFinishFlip = true;
        if (coinFlipAnimated != null) {
            coinFlipAnimated.setVisibility(View.INVISIBLE);
        }
        String resultedSide;
        CoinFlip flip;

        if (suggestedChildIndex != NO_CHILDREN_INT) {
            Child pickedChild = childrenList.get(lastSelectedChild);
            Bitmap portrait = pickedChild.getPortrait();
            flip = new CoinFlip(pickedChild, result, childPickedHead, portrait);
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
        updateSuggestedChildSharedPref(lastSelectedChild, this);

        updateCoinFlipHistorySharedPref();

        String resultMessage = "Result: " + resultedSide + " !\n" + flip.getPickerStatus();
        FragmentManager manager = getSupportFragmentManager();
        FlipCoinMessageFragment dialog = new FlipCoinMessageFragment(result, resultMessage);
        dialog.show(manager, "MessageDialog");
        onPause();
        populateChildrenList();
        isFinishFlip = false;
        flipped = false;
        Log.i("TAG", "Showed the dialog.");

        if (childrenList.size() != 0) {
            addHeadBtn.setVisibility(View.VISIBLE);
            addTailBtn.setVisibility(View.VISIBLE);
            pickStatus.setVisibility(View.VISIBLE);
            addHeadBtn.setBackgroundColor(0x45A364);
            addTailBtn.setBackgroundColor(0x45A364);
        }
        coinFlipActivate.setVisibility(View.VISIBLE);
        coinFlipAnimated.setVisibility(View.INVISIBLE);
        spinnerChildren.setClickable(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateCoinFlipHistorySharedPref() {
        Context ctx = getApplicationContext();
        String historyKey = ctx.getResources().getString(R.string.shared_pref_coin_flip_history_key);
        Helpers.saveObjectToSharedPreference(ctx, historyKey, coinFlipManager.getCoinFlipHistory());
    }

    public static void updateSuggestedChildSharedPref (int savedIndex, Context context) {
        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(context);
        String lastPickedChildKey = context.getResources().getString(R.string.shared_pref_suggested_child_key);
        editor.putInt(lastPickedChildKey, savedIndex);
        editor.apply();
    }

    public ArrayList<Integer> getAllChildrenID (Context mContext) {
        SharedPreferences prefs = Helpers.getSharedPreference(mContext);
        Gson gson = new Gson();
        String json = prefs.getString(childrenIDKey, "");
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        ArrayList<Integer> allChildrenID = gson.fromJson(json, type);
        return allChildrenID;
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
    protected void onPause() {
        childrenList.remove(childrenList.size() - 1);
        setArrayPrefs((ArrayList<Child>) childrenList,this);
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if (coinFlipSound != null) {
            coinFlipSound.stop();
            coinFlipSound.release();
            coinFlipSound = null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            isFinishFlip = true;
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //resultHead.setVisibility(View.INVISIBLE);
        //resultTail.setVisibility(View.INVISIBLE);
        //Toast.makeText(CoinFlipActivity.this,"On-back pressed in CoinFlip", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        spinnerChildren.setClickable(true);
        Log.e("TAG", "in onresume");
        //populateChildrenList();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onResume();
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

        attachButtonListeners();
    }
}