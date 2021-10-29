package com.example.parentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Timeout timer can count down time in 1 sec interval
 * and display reset, pause, resume buttons according
 * to different cases.
 *
 * "Keep the timer running while closing app" adapted from:
 *  https://www.youtube.com/watch?v=lvibl8YJfGo&list=PLrnPJCHvNZuB8wxqXCwKw2_NkyEmFwcSd&index=3
 */
public class TimerActivity extends AppCompatActivity {
    private TextView txtTimeCountDown;
    private ImageButton btnStart;
    private ImageButton btnReset;
    private ImageButton btnPause;
    private ImageButton btnResume;
    private ImageButton btnResetWhenStop;
    private Button btn1Min;
    private Button btn2Min;
    private Button btn3Min;
    private Button btn5Min;
    private Button btn10Min;
    private ImageView calmDown;
    private ImageView timeIsUp;
    private final long DEFAULT_DISPLAY_MILLIS = 10000;
    private final int COUNTDOWN_INTERVAL = 1000;
    private final int SIXTY = 60;
    private boolean isRunning = false;
    private CountDownTimer countDownTimer;
    private long timeLeftMills = DEFAULT_DISPLAY_MILLIS;

    public static Intent makeIntent(Context context) {
        return new Intent(context,TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setTitle(R.string.timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtTimeCountDown = findViewById(R.id.txtTimer);
        btnStart = findViewById(R.id.imgBtnStart);
        btnReset = findViewById(R.id.imgBtnReset);
        btnPause = findViewById(R.id.imgBtnPause);
        btnResume = findViewById(R.id.imgBtnResume);
        btnStart.setVisibility(View.VISIBLE);
        btnResetWhenStop = findViewById(R.id.imgBtnResetWhenStop);
        btn1Min = findViewById(R.id.btn1min);
        btn2Min = findViewById(R.id.btn2min);
        btn3Min = findViewById(R.id.btn3min);
        btn5Min = findViewById(R.id.btn5min);
        btn10Min = findViewById(R.id.btn10min);

        calmDown = findViewById(R.id.imgCalmDown); // Resource:https://www.clipartmax.com/middle/m2i8K9m2b1G6K9m2_calm-clip-art/
        timeIsUp = findViewById(R.id.imgTimeIsUp); // https://www.clipartmax.com/middle/m2i8N4b1m2N4d3Z5_when-the-district-operates-under-a-90-minute-delay-alarm-clock-clip/
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTimer();
                updateButtons();
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTimer();
                updateButtons();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                pauseTimer();
                updateButtons();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                resetTimer();
                pauseTimer();
            }
        });

        btnResetWhenStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                resetTimer();
            }
        });

        refreshCountDownText();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMills,COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                timeLeftMills = l;
                refreshCountDownText();
                if (timeLeftMills < 1000) {
                    isRunning = false;
                }
            }

            @Override
            public void onFinish() {
                if (timeLeftMills < 1000) {
                    isRunning = false;
                    updateButtons();
                }
            }
        }.start();
    }


    private void resetTimer() {
        timeLeftMills = DEFAULT_DISPLAY_MILLIS;
        refreshCountDownText();

        calmDown.setVisibility(View.INVISIBLE);
        timeIsUp.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.INVISIBLE);
        btnResetWhenStop.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
        btn1Min.setVisibility(View.VISIBLE);
        btn2Min.setVisibility(View.VISIBLE);
        btn3Min.setVisibility(View.VISIBLE);
        btn5Min.setVisibility(View.VISIBLE);
        btn10Min.setVisibility(View.VISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
    }

    private void refreshCountDownText() {
        int minutes = (int) timeLeftMills / COUNTDOWN_INTERVAL / SIXTY ;
        int seconds = (int)  timeLeftMills / COUNTDOWN_INTERVAL % SIXTY;
        String displayTimeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        txtTimeCountDown.setText(displayTimeLeft);
    }

    private void updateButtons(){
        if (isRunning){  // case when pressing "START" or "RESUME"
            calmDown.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.INVISIBLE);
            btn1Min.setVisibility(View.INVISIBLE);
            btn2Min.setVisibility(View.INVISIBLE);
            btn3Min.setVisibility(View.INVISIBLE);
            btn5Min.setVisibility(View.INVISIBLE);
            btn10Min.setVisibility(View.INVISIBLE);
        } else {
            btnPause.setVisibility(View.INVISIBLE);
            btnResetWhenStop.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            timeIsUp.setVisibility(View.VISIBLE);
            calmDown.setVisibility(View.INVISIBLE);
            if (timeLeftMills < 1000) { // case when the timer runs out of time
                btnResetWhenStop.setVisibility(View.VISIBLE);
                timeIsUp.setVisibility(View.VISIBLE);
                calmDown.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
                btnResume.setVisibility(View.INVISIBLE);
            } else {
                if (timeLeftMills < DEFAULT_DISPLAY_MILLIS) {// case when pressing "PAUSE"
                    btnPause.setVisibility(View.INVISIBLE);
                    timeIsUp.setVisibility(View.INVISIBLE);
                    calmDown.setVisibility(View.VISIBLE);
                    btnReset.setVisibility(View.VISIBLE);
                    btnResetWhenStop.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                }
                btnResume.setVisibility(View.VISIBLE);
            }
        }
    }
    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeft", timeLeftMills);
        outState.putBoolean("timerRunning",isRunning);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    } */
}
