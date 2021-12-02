package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.models.Helpers;
import com.github.florent37.viewanimator.ViewAnimator;

import java.util.Timer;
import java.util.TimerTask;


public class TakeBreathActivity extends AppCompatActivity {

    private int breaths = 3;
    private static final int MIN_RESPONSE_TIME = 3000;
    private static final int MAX_RESPONSE_TIME = 10000;
    private long timeElapsed = 0L;
    private boolean inBreathOutStage = false;
    private ViewAnimator animator;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        SharedPreferences sharedPref = Helpers.getSharedPreference(getApplicationContext());
        String breathsKey = getString(R.string.shared_pref_breaths_key);
        if (sharedPref.contains(breathsKey)) {
            this.breaths = sharedPref.getInt(breathsKey, 3);
            ((EditText)findViewById(R.id.editTextBreaths)).setText(Integer.toString(breaths));
        }

        updateTitle();
        addListeners();
    }

    private void addListeners() {
        TakeBreathActivity me = this;
        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(me, getString(R.string.breath_in), Toast.LENGTH_SHORT).show();
                me.toggleBreathUI(false);
            }
        });

        EditText editBreaths = findViewById(R.id.editTextBreaths);
        editBreaths.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String editedText = editable.toString();
                int breaths = 0;
                if (!Helpers.isStringNullOrEmpty(editedText)) {
                    breaths = Integer.parseInt(editable.toString());
                    if(breaths > 10) {
                        editable.replace(0, editable.length(), "10");
                        breaths = 10;
                    } else if(breaths < 1) {
                        editable.replace(0, editable.length(), "1");
                        breaths = 1;
                    }

                }

                me.breaths = breaths;
                updateTitle();
                SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(getApplicationContext());
                String breathsKey = getString(R.string.shared_pref_breaths_key);
                editor.putInt(breathsKey, me.breaths);

                editor.apply();
            }
        });

        Button beginBtn = findViewById(R.id.beginBtn);

        //noinspection AndroidLintClickableViewAccessibility
        beginBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleButtonDownEvents(event);
                return true;
            }
        });
    }

    private void toggleBreathUI(boolean initialUI) {
        Button startBtn = findViewById(R.id.startBtn);
        Button beginBtn = findViewById(R.id.beginBtn);
        TextView msg1 = findViewById(R.id.breathWelcomeMessage);
        TextView msg2 = findViewById(R.id.breathWelcomeMessage2);
        TextView helperMessage = findViewById(R.id.helperMessage);
        EditText breathCount = findViewById(R.id.editTextBreaths);
        ImageView circleIn = findViewById(R.id.circleIn);
        ImageView circleOut = findViewById(R.id.circleOut);


        circleOut.setVisibility(View.GONE);

        if (!initialUI) {
            startBtn.setVisibility(View.GONE);
            msg1.setVisibility(View.GONE);
            msg2.setVisibility(View.GONE);
            breathCount.setVisibility(View.GONE);
            beginBtn.setVisibility(View.VISIBLE);
            helperMessage.setVisibility(View.VISIBLE);
            circleIn.setVisibility(View.VISIBLE);
        } else {
            startBtn.setVisibility(View.VISIBLE);
            msg1.setVisibility(View.VISIBLE);
            msg2.setVisibility(View.VISIBLE);
            breathCount.setVisibility(View.VISIBLE);
            helperMessage.setVisibility(View.GONE);
            beginBtn.setVisibility(View.GONE);
            circleIn.setVisibility(View.GONE);

        }
    }

    private void updateTitle() {
        String title = getResources().getString(R.string.take_breath_heading);
        getSupportActionBar().setTitle(title.replace("N", Integer.toString(breaths)));
    }

    private void handleButtonDownEvents(MotionEvent event) {
        TakeBreathActivity me = TakeBreathActivity.this;

        Timer timer = new Timer();
        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                timeElapsed = event.getDownTime();
                me.beginCircleAnimation(getCircle(true));

                TimerTask textSwitchTask = new TimerTask() {
                    public void run() {

                        Button beginBtn = findViewById(R.id.beginBtn);
                        if (beginBtn != null) {
                            //at this stage inBreathOutStage has not been updated to prevent further complications downstream
                            String buttonMessage = inBreathOutStage ? getResources().getString(R.string.button_breath_in) : getResources().getString(R.string.button_breath_out);
                            beginBtn.setText(buttonMessage);
                        }
                    }
                };
                timer.schedule(textSwitchTask, MIN_RESPONSE_TIME);

                break;
            case MotionEvent.ACTION_UP:
                timeElapsed = event.getEventTime() - timeElapsed;
                me.stopCircleAnimation(getCircle(true));
                if (timeElapsed >= MIN_RESPONSE_TIME) {
                    if (inBreathOutStage) {
                        breaths--;
                        updateTitle();

                        if (breaths <= 0) {
                            ((EditText)findViewById(R.id.editTextBreaths)).setText("3");
                            me.toggleBreathUI(true);
                            return;
                        }
                    }

                    getCircle(true).setVisibility(View.GONE);
                    getCircle(false).setVisibility(View.VISIBLE);

                    me.inBreathOutStage = !inBreathOutStage;

                    String helperMessage = inBreathOutStage ? getResources().getString(R.string.breath_out) : getResources().getString(R.string.breath_in);
                    ((TextView)findViewById(R.id.helperMessage)).setText(helperMessage);

                    Toast.makeText(me, helperMessage, Toast.LENGTH_SHORT).show();
                } else {
                    timer.cancel();
                    Toast.makeText(TakeBreathActivity.this, "Hold for at least 3 seconds to advance.", Toast.LENGTH_SHORT).show();
                }

                timeElapsed = 0L;
                break;
        }
    }

    private ImageView getCircle(boolean isActive) {
        ImageView circleIn = findViewById(R.id.circleIn);
        ImageView circleOut = findViewById(R.id.circleOut);

        if (isActive) {
            return inBreathOutStage ? circleOut : circleIn;
        } else {
            return inBreathOutStage ? circleIn : circleOut;
        }
    }

    private void beginCircleAnimation(ImageView circle) {
        Float scale = inBreathOutStage ? 0.5f : 1;

        animator = ViewAnimator
                .animate(circle)
                .scale(scale)
                .rotation(720)
                .duration(MAX_RESPONSE_TIME)
                .start();
    }

    private void stopCircleAnimation(ImageView circle) {
        if (inBreathOutStage) {
            circle.setScaleX(1);
            circle.setScaleY(1);
        } else {
            circle.setScaleX(0.5f);
            circle.setScaleY(0.5f);
        }

        circle.setRotation(0);
        if (animator != null) {
            animator.cancel();
        }
    }
}