package com.example.parentapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.parentapp.models.Helpers;
import java.util.Locale;

/**
 * Timeout timer can count down time in 1 sec interval
 * and display reset, pause, resume buttons according
 * to different cases.
 * "Countdown Timer" adapted from (with some modifications):
 * https://www.youtube.com/watch?v=MDuGwI6P-X8&list=PLrnPJCHvNZuB8wxqXCwKw2_NkyEmFwcSd&index=1
 * "Keep the timer running while closing app" adapted from (with some modifications):
 *  https://www.youtube.com/watch?v=lvibl8YJfGo&list=PLrnPJCHvNZuB8wxqXCwKw2_NkyEmFwcSd&index=3
 *  "Send notification when timer done":
 *  https://www.tutorialspoint.com/how-to-create-android-notification-with-broadcastreceiver
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
    private long timeInMills;
    private final int COUNTDOWN_INTERVAL = 1000;
    private final int SIXTY = 60;
    private boolean isRunning = false;
    private CountDownTimer countDownTimer;
    private long timeLeftMills;
    private long endTime;
    private final String TIME_LEFT = "timeLeft";
    private final String IS_TIMER_RUNNING = "timerRunning";
    private final String END_TIME = "endTime";
    public static final String SEND_NOTIFICATION_ID = "sendNotification";
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "default" ;
    public final static String SEND_TITLE = "Time is up!";
    private AlarmManager alarmManager;
    public static Intent makeIntent(Context context) {
        return new Intent(context,TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setTitle(R.string.timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timeInMills = 10000;
        timeLeftMills = timeInMills;
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
        alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTimer();
                scheduleNotification(getNotification()) ;
                updateButtons();
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
    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftMills;
        countDownTimer = new CountDownTimer(timeLeftMills,COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long l) {
                timeLeftMills = l;
                refreshCountDownText();
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
        timeLeftMills = timeInMills;
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
        Intent notificationIntent = new Intent( this, NotificationReceiver. class) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        updateButtons();
        refreshCountDownText();
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
        } else {  // timer is not running -> does not start/ PAUSE/ end
            btnPause.setVisibility(View.INVISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            calmDown.setVisibility(View.INVISIBLE);
            if (timeLeftMills < COUNTDOWN_INTERVAL) { // case when the timer runs out of time
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
            } else { // still have remaining time but is not running
                if (timeLeftMills < timeInMills) { // case when pressing "PAUSE"
                    btnPause.setVisibility(View.INVISIBLE);
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
        long futureInMillis = System.currentTimeMillis() + timeLeftMills;
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

    @Override
    protected void onStop() {
        super.onStop();
        if (isRunning) {
            countDownTimer.cancel();
        }
        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(getApplicationContext());
        editor.putLong(TIME_LEFT,timeLeftMills);
        editor.putBoolean(IS_TIMER_RUNNING, isRunning);
        editor.putLong(END_TIME,endTime);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = Helpers.getSharedPreference(getApplicationContext());
        timeLeftMills = prefs.getLong(TIME_LEFT,timeInMills);
        isRunning = prefs.getBoolean(IS_TIMER_RUNNING,false);
        updateButtons();
        if (isRunning) {
            endTime = prefs.getLong(END_TIME,0);
            timeLeftMills = endTime - System.currentTimeMillis();
            if (timeLeftMills < 0) {
                timeLeftMills = 0;
                isRunning = false;
                refreshCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
        refreshCountDownText();
    }
}
