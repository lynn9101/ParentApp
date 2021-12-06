package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
    private Timer timer;
    private MediaPlayer media;

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
        Button beginBtn = findViewById(R.id.beginBtn);
        Button transitionBtn = findViewById(R.id.transitionBtn);
        EditText editBreaths = findViewById(R.id.editTextBreaths);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (breaths <= 0) {
                    ((EditText) findViewById(R.id.editTextBreaths)).setText("3");
                }
                me.toggleBreathUI(false);
            }
        });

        transitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null) {
                    timer.cancel();
                }
                handleStageTransition();
            }
        });

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
                    String message = "Number of breaths is between 1 and 10.";
                    if (breaths > 10) {
                        Toast.makeText(TakeBreathActivity.this, message, Toast.LENGTH_SHORT)
                                .show();
                        editable.replace(0, editable.length(), "10");
                        breaths = 10;
                    } else if (breaths < 1) {
                        Toast.makeText(TakeBreathActivity.this, message, Toast.LENGTH_SHORT)
                                .show();
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

        //noinspection AndroidLintClickableViewAccessibility
        beginBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleInButtonDownEvent(event);
                return true;
            }
        });
    }

    private void toggleBreathUI(boolean initialUI) {
        Button startBtn = findViewById(R.id.startBtn);
        Button beginBtn = findViewById(R.id.beginBtn);
        Button transitionBtn = findViewById(R.id.transitionBtn);
        TextView msg1 = findViewById(R.id.breathWelcomeMessage);
        TextView msg2 = findViewById(R.id.breathWelcomeMessage2);
        TextView helperMessage = findViewById(R.id.helperMessage);
        EditText breathCount = findViewById(R.id.editTextBreaths);
        ImageView circleIn = findViewById(R.id.circleIn);
        ImageView circleOut = findViewById(R.id.circleOut);

        transitionBtn.setEnabled(false);
        transitionBtn.setVisibility(View.GONE);
        circleOut.setVisibility(View.GONE);

        if (!initialUI) {
            beginBtn.setText(getString(R.string.button_breath_in));
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

    private void handleInButtonDownEvent(MotionEvent event) {
        TakeBreathActivity me = TakeBreathActivity.this;

        switch (event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                inBreathOutStage = false;
                timer = new Timer();
                timeElapsed = event.getDownTime();
                me.beginCircleAnimation(getCircle(true));

                TimerTask textSwitchTask = new TimerTask() {
                    public void run() {
                        Button beginBtn = findViewById(R.id.beginBtn);
                        if (beginBtn != null) {
                            //at this stage inBreathOutStage has not been updated to prevent further complications downstream
                            String buttonMessage = getString(R.string.button_breath_out);
                            beginBtn.setText(buttonMessage);
                            displayThreadedToast(buttonMessage);
                        }
                    }
                };
                timer.schedule(textSwitchTask, MIN_RESPONSE_TIME);

                TimerTask helperTextSwitchTask = new TimerTask() {
                    public void run() {
                        TextView helperMessage = findViewById(R.id.helperMessage);
                        if (helperMessage != null) {
                            String msg = getString(R.string.breath_in_transition_help);
                            helperMessage.setText(msg);
                        }
                        media.stop();
                    }
                };
                timer.schedule(helperTextSwitchTask, MAX_RESPONSE_TIME);

                break;
            case MotionEvent.ACTION_UP:
                timeElapsed = event.getEventTime() - timeElapsed;
                me.stopCircleAnimation(getCircle(true));
                if (timeElapsed >= MIN_RESPONSE_TIME) {
                    ((Button)findViewById(R.id.beginBtn)).setEnabled(false);
                    ((TextView)findViewById(R.id.helperMessage)).setText(R.string.breath_out_indicate_message);

                    getCircle(true).setVisibility(View.GONE);
                    getCircle(false).setVisibility(View.VISIBLE);

                    inBreathOutStage = true;
                    me.beginCircleAnimation(getCircle(true));

                    timer.cancel();
                    timer = new Timer();

                    TimerTask stageTransitionTaskOne = new TimerTask() {
                        public void run() {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    breaths--;
                                    updateTitle();
                                    //avoid using closure since beginBtn could be null at this stage
                                    Button beginBtn = findViewById(R.id.beginBtn);

                                    beginBtn.setEnabled(true);
                                    Button transitionBtn = findViewById(R.id.transitionBtn);

                                    if (breaths > 0) {
                                        beginBtn.setText(getString(R.string.button_breath_in));
                                        transitionBtn.setText(getString(R.string.button_breath_skip));
                                    } else {
                                        transitionBtn.setText(getString(R.string.button_breath_done));
                                    }

                                    beginBtn.setVisibility(View.GONE);
                                    transitionBtn.setVisibility(View.VISIBLE);
                                    transitionBtn.setEnabled(true);
                                }
                            });
                        }
                    };
                    timer.schedule(stageTransitionTaskOne, MIN_RESPONSE_TIME);

                    TimerTask stageTransitionTaskTwo = new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    handleStageTransition();
                                }
                            });
                        }
                    };
                    timer.schedule(stageTransitionTaskTwo, MAX_RESPONSE_TIME);

                } else {
                    timer.cancel();
                    Toast.makeText(TakeBreathActivity.this, "Hold for at least 3 seconds to advance.", Toast.LENGTH_SHORT).show();
                }

                timeElapsed = 0L;
                break;
        }
    }

    private void handleStageTransition() {
        Button beginBtn = findViewById(R.id.beginBtn);
        Button transitionBtn = findViewById(R.id.transitionBtn);

        if (inBreathOutStage) {
            this.stopCircleAnimation(getCircle(true));
            inBreathOutStage = false;
            ((TextView) findViewById(R.id.helperMessage)).setText(getString(R.string.breath_in));

            if (breaths <= 0) {
                ((EditText) findViewById(R.id.editTextBreaths)).setText("3");
                this.toggleBreathUI(true);
            } else {
                getCircle(true).setVisibility(View.VISIBLE);
                getCircle(false).setVisibility(View.GONE);

                transitionBtn.setEnabled(false);
                transitionBtn.setVisibility(View.GONE);
                beginBtn.setVisibility(View.VISIBLE);
            }
        } else {
            inBreathOutStage = false;
        }
    }

    private void updateTitle() {
        String title = getString(R.string.take_breath_heading);
        getSupportActionBar().setTitle(title.replace("N", Integer.toString(breaths)));
    }

    private void displayThreadedToast(String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(TakeBreathActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
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
        AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        if (inBreathOutStage) {
            media = Helpers.getMediaPlayer(TakeBreathActivity.this, R.raw.breath_out);

        } else {
            media = Helpers.getMediaPlayer(TakeBreathActivity.this, R.raw.breath_in);
        }
        media.setLooping(true);
        media.start();

        animator = ViewAnimator
                .animate(circle)
                .scale(scale)
                .rotation(720)
                .duration(MAX_RESPONSE_TIME)
                .start();
    }

    private void stopCircleAnimation(ImageView circle) {
        if (media != null) {
            media.stop();
        }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (media != null) {
            media.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (media != null) {
            media.stop();
        }
    }
}