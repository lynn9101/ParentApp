package com.example.parentapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentapp.models.Helpers;
import java.util.Locale;

/**
 * Timeout timer can count down time in 1 sec interval
 * and display reset, pause, resume buttons according
 * to different cases. The timer also displays dynamic
 * visualization when it is activated.
 */
public class TimerActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CONTENT = "Time is up!";
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
    private EditText customMinutes;
    private Button confirmMinutes;
    private TextView speedTv;
    private long timeInMills;
    private final long SECOND_TO_MIL = 1000;
    private long COUNTDOWN_INTERVAL = SECOND_TO_MIL;
    private final int HOURS_TO_SECONDS = 3600;
    private final int SIXTY = 60;
    private final int MINS_TO_MILLS = 60000;
    private final int TWO_MINS_TO_MILLS = 120000;
    private final int THREE_MINS_TO_MILLS = 180000;
    private final int FIVE_MINS_TO_MILLS = 300000;
    private final int TEN_MINS_TO_MILLS = 600000;
    private boolean isRunning = false;
    private CountDownTimer countDownTimer;
    private long actualMilliRemaining;
    private long endTime;
    private long oldTimerRemainingTime;
    private int oldTimerProgress;
    private final String TIME_LEFT = "timeLeft";
    private final String IS_TIMER_RUNNING = "timerRunning";
    private final String END_TIME = "endTime";
    private final String CUSTOM_TIME = "customTimer";
    private final String SPEED_PERCENTAGE = "speedPercentage";
    private final String OLD_TIMER_REM_TIME = "oldRemainTimeForTimer";
    private final String OLD_TIMER_PROGRESS_BAR = "oldProgressBarForTimer";
    private final String OLD_TIMER_INTERVAL = "oldIntervalForTimer";
    public static final String SEND_NOTIFICATION_ID = "sendNotification";
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "default" ;
    public final static String SEND_TITLE = "Time is up!";
    private AlarmManager alarmManager;
    private int progress = 0;
    private ProgressBar timerSpinner;
    private final String SPINNER_PROGRESS = "spinner1";
    private int speedPercentage = 100;

    public static Intent makeIntent(Context context) {
        return new Intent(context,TimerActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timer_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int speed;
        switch (item.getItemId()){
            case R.id.spd1:
                speed = 25;
                break;
            case R.id.spd2:
                speed = 50;
                break;
            case R.id.spd3:
                speed = 75;
                break;
            case R.id.spd4:
                speed = 100;
                break;
            case R.id.spd5:
                speed = 200;
                break;
            case R.id.spd6:
                speed = 300;
                break;
            case R.id.spd7:
                speed = 400;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        Log.d("onOptionsItemSelected", "kfate - speedPercentage : " + speedPercentage);
        Log.d("onOptionsItemSelected", "kfate - timeInMills/COUNTDOWN_INTERVAL : " + timeInMills/COUNTDOWN_INTERVAL);
        changeTimerSpeed(speed);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getWindow().addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setTitle(R.string.timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        timeInMills = MINS_TO_MILLS;
        actualMilliRemaining = timeInMills;
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
        speedTv = findViewById(R.id.currentSpd);
        calmDown = findViewById(R.id.imgCalmDown);
        timeIsUp = findViewById(R.id.imgTimeIsUp);
        alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE);
        customMinutes = findViewById(R.id.editTextSetMinutes);
        confirmMinutes = findViewById(R.id.btnConfirmMinutes);
        timerSpinner = findViewById(R.id.timerSpinner);
        timerSpinner.setVisibility(View.INVISIBLE);

        updateProgressBar(progress);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTimer();
                scheduleNotification(getNotification()) ;
                updateProgressBar(progress);
                updateButtons();
            }
        });

        btn1Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long millisInput = MINS_TO_MILLS;
                setTime(millisInput);
                customMinutes.setText("");
            }
        });

        btn2Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long millisInput = TWO_MINS_TO_MILLS;
                setTime(millisInput);
                customMinutes.setText("");
            }
        });

        btn3Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long millisInput = THREE_MINS_TO_MILLS;
                setTime(millisInput);
                customMinutes.setText("");
            }
        });

        btn5Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long millisInput = FIVE_MINS_TO_MILLS;
                setTime(millisInput);
                customMinutes.setText("");
            }
        });

        btn10Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long millisInput = TEN_MINS_TO_MILLS;
                setTime(millisInput);
                customMinutes.setText("");
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTimer();
                scheduleNotification(getNotification()) ;
                updateButtons();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });

        btnResetWhenStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                resetTimer();
            }
        });

        confirmMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = customMinutes.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(TimerActivity.this, "The field cannot be EMPTY!", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * MINS_TO_MILLS;
                if (millisInput == 0) {
                    Toast.makeText(TimerActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                customMinutes.setText("");
            }
        });
    }

    private void updateProgressBar(int spinner_progress) {
        timerSpinner.setMax((int) ((int) timeInMills / COUNTDOWN_INTERVAL)); //unsure about this change
        timerSpinner.setProgress(spinner_progress);
    }

    private void setTime(long milliseconds) {
        timeInMills = milliseconds;
        resetTimer();
    }

    private void startTimer() {
        timerSpinner.setVisibility(View.VISIBLE);
        speedTv.setVisibility(View.VISIBLE);
        endTime = System.currentTimeMillis() + actualMilliRemaining;
        String s = "kfate - timeLeftMills/actualMiliRemaining : " + actualMilliRemaining +
                "\n COUNTDOWN_INTERVAL : " + COUNTDOWN_INTERVAL;
        Toast. makeText(getApplicationContext(),s , Toast.LENGTH_SHORT).show();
        Log.d("startTimer", s);
        countDownTimer = new CountDownTimer(actualMilliRemaining,COUNTDOWN_INTERVAL) {
            int numberOfSeconds = (int)(timeInMills/COUNTDOWN_INTERVAL);
            @Override
            public void onTick(long l) {
                Toast. makeText(getApplicationContext(),"seconds remaining: " + l / COUNTDOWN_INTERVAL + "\n interval is" + COUNTDOWN_INTERVAL, Toast.LENGTH_SHORT).show();
                actualMilliRemaining = l;
                int fakeDisplaySecondsRemaining = (int) (l/COUNTDOWN_INTERVAL);
                int realSecondsRemaining = (int) (l/SECOND_TO_MIL);
                refreshCountDownText(fakeDisplaySecondsRemaining);
//                int secondsRemaining = (int) (l/ COUNTDOWN_INTERVAL);
                Log.d("onTick", "kfate - starter - actualSecondsRemaining : " + fakeDisplaySecondsRemaining);
                Log.d("onTick", "kfate - starter - displaySecondsRemaining : " + realSecondsRemaining);
                progress = numberOfSeconds - ((numberOfSeconds-fakeDisplaySecondsRemaining));
                oldTimerRemainingTime = fakeDisplaySecondsRemaining;
                oldTimerProgress = progress;
                timerSpinner.setProgress(progress);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                updateButtons();
            }
        }.start();
    }

    private void resetTimer() {
        if (isRunning) {
            pauseTimer();
        }
        actualMilliRemaining = timeInMills;
        refreshCountDownText(actualMilliRemaining);
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
        customMinutes.setVisibility(View.VISIBLE);
        confirmMinutes.setVisibility(View.VISIBLE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(SEND_NOTIFICATION_ID);

        timerSpinner.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
        Intent notificationIntent = new Intent( this, NotificationReceiver. class) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        updateButtons();
        refreshCountDownText(actualMilliRemaining);
    }

    private void refreshCountDownText(long displaySecondsRemaining) {
        int hours = (int) (displaySecondsRemaining / COUNTDOWN_INTERVAL) / HOURS_TO_SECONDS;
        int minutes = (int) ((displaySecondsRemaining / COUNTDOWN_INTERVAL) % HOURS_TO_SECONDS) / SIXTY;
        int seconds = (int)  (displaySecondsRemaining / COUNTDOWN_INTERVAL) % SIXTY;
        String displayTimeLeft;
        if (hours > 0) {
            displayTimeLeft = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, seconds);
        } else {
            displayTimeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        }
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
            customMinutes.setVisibility(View.INVISIBLE);
            confirmMinutes.setVisibility(View.INVISIBLE);
        } else {  // timer is not running -> does not start/ PAUSE/ end
            btnPause.setVisibility(View.INVISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            calmDown.setVisibility(View.INVISIBLE);
            if (actualMilliRemaining < COUNTDOWN_INTERVAL) { // case when the timer runs out of time
                timerSpinner.setVisibility(View.INVISIBLE);
                btnResetWhenStop.setVisibility(View.VISIBLE);
                timeIsUp.setVisibility(View.VISIBLE);
                calmDown.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
                btnResume.setVisibility(View.INVISIBLE);
                btn1Min.setVisibility(View.INVISIBLE);
                btn2Min.setVisibility(View.INVISIBLE);
                btn3Min.setVisibility(View.INVISIBLE);
                btn5Min.setVisibility(View.INVISIBLE);
                btn10Min.setVisibility(View.INVISIBLE);
                customMinutes.setVisibility(View.INVISIBLE);
                confirmMinutes.setVisibility(View.INVISIBLE);
            } else { // still have remaining time but is not running
                if (actualMilliRemaining < timeInMills) { // case when pressing "PAUSE"
                    btnPause.setVisibility(View.INVISIBLE);
                    timerSpinner.setVisibility(View.VISIBLE);
                    timeIsUp.setVisibility(View.INVISIBLE);
                    calmDown.setVisibility(View.VISIBLE);
                    btnReset.setVisibility(View.VISIBLE);
                    btnResetWhenStop.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                    btnResume.setVisibility(View.VISIBLE);
                    btn1Min.setVisibility(View.INVISIBLE);
                    btn2Min.setVisibility(View.INVISIBLE);
                    btn3Min.setVisibility(View.INVISIBLE);
                    btn5Min.setVisibility(View.INVISIBLE);
                    btn10Min.setVisibility(View.INVISIBLE);
                    customMinutes.setVisibility(View.INVISIBLE);
                    confirmMinutes.setVisibility(View.INVISIBLE);
                } else {
                    btnResume.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

    private void scheduleNotification (Notification notification) {
        Intent notificationIntent = new Intent( this, NotificationReceiver. class) ;
        notificationIntent.putExtra(NotificationReceiver. CHANNEL_ID , 1) ;
        notificationIntent.putExtra(NotificationReceiver. NOTIFICATION_ID , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT);
        long futureInMillis = System.currentTimeMillis() + actualMilliRemaining;
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis , pendingIntent);
    }
    private Notification getNotification () {
        Intent notificationIntent = new Intent( this, TimerActivity. class) ;
        PendingIntent pendingIntent = PendingIntent. getActivity ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, DEFAULT_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(SEND_TITLE);
        builder.setContentIntent(pendingIntent);
        builder.setContentText(NOTIFICATION_CONTENT);
        builder.setSmallIcon(R.drawable. ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setFullScreenIntent(pendingIntent,true);
        Uri soundUri = Uri.parse("android.resource://" + TimerActivity.this.getPackageName() + "/" + R.raw.notification_music);
        builder.setSound(soundUri);
        builder.setChannelId(SEND_NOTIFICATION_ID);
        return builder.build() ;
    }

    private void changeTimerSpeed(int speed){
        //update the slider and timer
        //record the new remaining time
        //update interval

        String s = "kfate - COUNTDOWN_INTERVAL :";
        Log.d("changeTimerSpeed", "before - " + s + COUNTDOWN_INTERVAL);

        this.speedPercentage = speed;
        speedTv.setText(speedPercentage + "% speed");
        this.COUNTDOWN_INTERVAL = (long) (SECOND_TO_MIL * ((100 * 1.0)/speedPercentage));

        if(countDownTimer != null) {
            //if this change of speed is for an existing timer
            countDownTimer.cancel();
            endTime = System.currentTimeMillis() + actualMilliRemaining;
            countDownTimer = new CountDownTimer(actualMilliRemaining, COUNTDOWN_INTERVAL) {
                int numberOfSeconds = (int)(timeInMills/COUNTDOWN_INTERVAL);
                @Override
                public void onTick(long l) {
                    actualMilliRemaining = l;
                    int fakeDisplaySecondsRemaining = (int) (l/COUNTDOWN_INTERVAL);
                    int realSecondsRemaining = (int) (l/SECOND_TO_MIL);
                    refreshCountDownText(fakeDisplaySecondsRemaining);
                    Log.d("onTick", "kfate - CHANGED - actualSecondsRemaining : " + fakeDisplaySecondsRemaining);
                    Log.d("onTick", "kfate - CHANGED - displaySecondsRemaining : " + realSecondsRemaining);
                    progress = numberOfSeconds - ((numberOfSeconds-realSecondsRemaining));
                    oldTimerRemainingTime = fakeDisplaySecondsRemaining;
                    oldTimerProgress = progress;
                    timerSpinner.setProgress(progress);
                }

                @Override
                public void onFinish() {
                    isRunning = false;
                    updateButtons();
                }
            }.start();
        }

        Log.d("changeTimerSpeed" , "after  - " + s + COUNTDOWN_INTERVAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRunning) {
            countDownTimer.cancel();
        }
        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(getApplicationContext());
        editor.putLong(TIME_LEFT, actualMilliRemaining);
        editor.putBoolean(IS_TIMER_RUNNING, isRunning);
        editor.putLong(END_TIME,endTime);
        editor.putLong(CUSTOM_TIME,timeInMills);
        editor.putInt(SPINNER_PROGRESS,progress);
        editor.putInt(SPEED_PERCENTAGE,speedPercentage);
        editor.putLong(OLD_TIMER_REM_TIME, oldTimerRemainingTime);
        editor.putInt(OLD_TIMER_PROGRESS_BAR, oldTimerProgress);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = Helpers.getSharedPreference(getApplicationContext());
        timeInMills = prefs.getLong(CUSTOM_TIME, MINS_TO_MILLS);
        actualMilliRemaining = prefs.getLong(TIME_LEFT,timeInMills);
        isRunning = prefs.getBoolean(IS_TIMER_RUNNING,false);
        progress = prefs.getInt(SPINNER_PROGRESS, 0);
        updateButtons();
        updateProgressBar(progress);

        if (isRunning) {
            endTime = prefs.getLong(END_TIME,0);
            actualMilliRemaining = endTime - System.currentTimeMillis();
            if (actualMilliRemaining < 0) {
                actualMilliRemaining = 0;
                isRunning = false;
                refreshCountDownText(actualMilliRemaining);
                updateButtons();
            } else {
                timerSpinner.setVisibility(View.VISIBLE);
                startTimer();
            }
        }
        refreshCountDownText(actualMilliRemaining);
    }
}
