package com.example.parentapp;

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

public class TimerActivity extends AppCompatActivity {
    private TextView txtTimeCountDown;
    private ImageButton btnStart;
    private ImageButton btnReset;
    private ImageButton btnPause;
    private ImageButton btnResume;
    private Button btn1Min;
    private Button btn2Min;
    private Button btn3Min;
    private Button btn5Min;
    private Button btn10Min;
    private ImageView calmDown;
    private final long DEFAULT_DISPLAY_MILLIS = 600000;
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

        btn1Min = findViewById(R.id.btn1min);
        btn2Min = findViewById(R.id.btn2min);
        btn3Min = findViewById(R.id.btn3min);
        btn5Min = findViewById(R.id.btn5min);
        btn10Min = findViewById(R.id.btn10min);

        calmDown = findViewById(R.id.imgCalmDown); // Resource:https://www.clipartmax.com/middle/m2i8K9m2b1G6K9m2_calm-clip-art/
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                calmDown.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                btn1Min.setVisibility(View.INVISIBLE);
                btn2Min.setVisibility(View.INVISIBLE);
                btn3Min.setVisibility(View.INVISIBLE);
                btn5Min.setVisibility(View.INVISIBLE);
                btn10Min.setVisibility(View.INVISIBLE);
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                btnResume.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                btnPause.setVisibility(View.INVISIBLE);
                btnResume.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                pauseTimer();
                calmDown.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
                btn1Min.setVisibility(View.VISIBLE);
                btn2Min.setVisibility(View.VISIBLE);
                btn3Min.setVisibility(View.VISIBLE);
                btn5Min.setVisibility(View.VISIBLE);
                btn10Min.setVisibility(View.VISIBLE);
            }
        });

        refreshCountDownText();
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMills,1000) {
            @Override
            public void onTick(long l) {
                timeLeftMills = l;
                refreshCountDownText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                btnStart.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
            }
        }.start();
        isRunning = true;
        btnPause.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);
    }


    private void resetTimer() {
        timeLeftMills = DEFAULT_DISPLAY_MILLIS;
        refreshCountDownText();
        btnReset.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
    }

    private void refreshCountDownText() {
        int minutes = (int) timeLeftMills / 1000 / 60 ;
        int seconds = (int) timeLeftMills / 1000 % 60;
        String displayTimeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        txtTimeCountDown.setText(displayTimeLeft);
    }
}
