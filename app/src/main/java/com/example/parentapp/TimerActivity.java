package com.example.parentapp;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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
    public static final String SPEED_LESS_THAN_100 = "speed less than 100%";
    public static final String SPEED_PERCENTAGE = "Speed Percentage";
    public static final String ACTUAL_REMAINING_TIME = "Actual Remaining Time";
    public static final String ACTUAL_TIME_INTERVAL = "Actual Time Interval";
    public static final String DISPLAY_SPEED_PERCENTAGE = "Display Speed Percentage";
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
    private long timeInMills;
    private final int COUNTDOWN_INTERVAL = 1000;
    private final int MINS_TO_MILLS = 60000;
    private final int TWO_MINS_TO_MILLS = 120000;
    private final int THREE_MINS_TO_MILLS = 180000;
    private final int FIVE_MINS_TO_MILLS = 300000;
    private final int TEN_MINS_TO_MILLS = 600000;
    private boolean isRunning = false;
    private CountDownTimer countDownTimer;
    private long timeLeftMills;
    private long endTime;
    private final String TIME_LEFT = "timeLeft";
    private final String IS_TIMER_RUNNING = "timerRunning";
    private final String END_TIME = "endTime";
    private final String CUSTOM_TIME = "customTimer";
    public static final String SEND_NOTIFICATION_ID = "sendNotification";
    private final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "default";
    public final static String SEND_TITLE = "Time is up!";
    private AlarmManager alarmManager;
    private int progress = 0;
    private ProgressBar timerSpinner;
    private final String SPINNER_PROGRESS = "spinner1";
    private int speedPercentage;
    private String timerPercentage;
    private TextView displayTimerPercentage;
    private long actualRemainingTime;
    private long actualCountDownInterval;
    private boolean slowThan100Percent;

    private int decreaseSeconds = 1;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spd1:
                speedPercentage = 4;
                timerPercentage = "Time @25%";
                actualRemainingTime = timeLeftMills * speedPercentage;
                actualCountDownInterval = COUNTDOWN_INTERVAL * speedPercentage;
                slowThan100Percent = true;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            case R.id.spd2:
                speedPercentage = 2;
                timerPercentage = "Time @50%";
                actualRemainingTime = timeLeftMills * speedPercentage;
                actualCountDownInterval = COUNTDOWN_INTERVAL * speedPercentage;
                slowThan100Percent = true;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            case R.id.spd3:
                speedPercentage = (4 / 3);
                timerPercentage = "Time @75%";
                actualRemainingTime = timeLeftMills * speedPercentage;
                actualCountDownInterval = COUNTDOWN_INTERVAL * speedPercentage;
                slowThan100Percent = true;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            case R.id.spd4:
                speedPercentage = 1;
                timerPercentage = "Time @100%";
                actualRemainingTime = timeLeftMills;
                actualCountDownInterval = COUNTDOWN_INTERVAL;
                slowThan100Percent = false;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            case R.id.spd5:
                speedPercentage = 2;
                timerPercentage = "Time @200%";
                actualRemainingTime = timeLeftMills / speedPercentage;
                actualCountDownInterval = COUNTDOWN_INTERVAL / speedPercentage;
                slowThan100Percent = false;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            case R.id.spd6:
                speedPercentage = 3;
                timerPercentage = "Time @300%";
                actualRemainingTime = timeLeftMills / speedPercentage;
                actualCountDownInterval = COUNTDOWN_INTERVAL / speedPercentage;
                slowThan100Percent = false;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            case R.id.spd7:
                speedPercentage = 4;
                timerPercentage = "Time @400%";
                actualRemainingTime = timeLeftMills / speedPercentage;
                actualCountDownInterval = COUNTDOWN_INTERVAL / speedPercentage;
                slowThan100Percent = false;
                displayTimerPercentage.setText(timerPercentage);
                saveInfoForOldTimer();
                fetchInfoForNewTimer();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setTitle(R.string.timer_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        setUpTimerComponents();
        updateProgressBar(progress);
        attachButtonsListener();
    }

    private void setUpTimerComponents() {
        speedPercentage = 1;
        timeInMills = MINS_TO_MILLS;
        timeLeftMills = timeInMills;
        actualRemainingTime = timeLeftMills;
        actualCountDownInterval = COUNTDOWN_INTERVAL;

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
        calmDown = findViewById(R.id.imgCalmDown);
        timeIsUp = findViewById(R.id.imgTimeIsUp);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        customMinutes = findViewById(R.id.editTextSetMinutes);
        confirmMinutes = findViewById(R.id.btnConfirmMinutes);
        timerSpinner = findViewById(R.id.timerSpinner);
        timerSpinner.setVisibility(View.INVISIBLE);
        displayTimerPercentage = findViewById(R.id.txtTimerPercentage);
        timerPercentage = "Time @100%";
        displayTimerPercentage.setText(timerPercentage);
    }

    private void attachButtonsListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTimer(actualCountDownInterval, actualRemainingTime);
                scheduleNotification(getNotification());
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
                startTimer(actualCountDownInterval,actualRemainingTime);
                scheduleNotification(getNotification());
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

    private void updateProgressBar(int spinnerProgress) {
        timerSpinner.setProgress(spinnerProgress);
        int newMax = (int) timeInMills / 1000 * speedPercentage;
        if (!slowThan100Percent) {
            newMax = (int) timeInMills / 1000 / speedPercentage;
        }
        timerSpinner.setMax(newMax);
    }

    private void setTime(long milliseconds) {
        timeInMills = milliseconds;
        resetTimer();
    }

    private void startTimer(long newCountDownInterval, long newRemainingTime) {

        timerSpinner.setVisibility(View.VISIBLE);
        endTime = System.currentTimeMillis() + newRemainingTime;
        countDownTimer = new CountDownTimer(newRemainingTime, newCountDownInterval) {

            @Override
            public void onTick(long l) {
                timeLeftMills = l / speedPercentage;
                if (!slowThan100Percent) {
                    timeLeftMills = l * speedPercentage;
                }

                actualRemainingTime = timeLeftMills * speedPercentage;
                if (!slowThan100Percent) {
                    actualRemainingTime = timeLeftMills / speedPercentage;
                }

                int secondsRemaining = (int) l / 1000;
                progress = secondsRemaining;
                updateProgressBar(progress);
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
        actualRemainingTime = timeLeftMills;
        actualCountDownInterval = COUNTDOWN_INTERVAL;
        speedPercentage = 1;
        progress = 0;
        timerPercentage = "Time @100%";
        displayTimerPercentage.setText(timerPercentage);
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
        customMinutes.setVisibility(View.VISIBLE);
        confirmMinutes.setVisibility(View.VISIBLE);
        timerSpinner.setVisibility(View.INVISIBLE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(SEND_NOTIFICATION_ID);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        updateButtons();
        refreshCountDownText();
    }

    private void refreshCountDownText() {
        final int HOURS_TO_SECONDS = 3600;
        final int SIXTY = 60;
        int hours = (int) (timeLeftMills / COUNTDOWN_INTERVAL) / HOURS_TO_SECONDS;
        int minutes = (int) ((timeLeftMills / COUNTDOWN_INTERVAL) % HOURS_TO_SECONDS) / SIXTY;
        int seconds = (int) (timeLeftMills / COUNTDOWN_INTERVAL) % SIXTY;

        String displayTimeLeft;
        if (hours > 0) {
            displayTimeLeft = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            displayTimeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        txtTimeCountDown.setText(displayTimeLeft);
    }

    private void updateButtons() {
        if (isRunning) {  // case when pressing "START" or "RESUME"
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
            if (timeLeftMills < COUNTDOWN_INTERVAL) { // case when the timer runs out of time
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
                if (timeLeftMills < timeInMills) { // case when pressing "PAUSE"
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

    private void scheduleNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.CHANNEL_ID, 1);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = System.currentTimeMillis() + actualRemainingTime;
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(SEND_TITLE);
        builder.setContentIntent(pendingIntent);
        builder.setContentText(NOTIFICATION_CONTENT);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setFullScreenIntent(pendingIntent, true);
        Uri soundUri = Uri.parse("android.resource://" + TimerActivity.this.getPackageName() + "/" + R.raw.notification_music);
        builder.setSound(soundUri);
        builder.setChannelId(SEND_NOTIFICATION_ID);
        return builder.build();
    }

    private void saveInfoForOldTimer() {
        if (isRunning) {
            countDownTimer.cancel();
            Intent notificationIntent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            scheduleNotification(getNotification());
        }
        endTime = System.currentTimeMillis() + actualRemainingTime;

        SharedPreferences.Editor editor = Helpers.getSharedPrefEditor(getApplicationContext());
        editor.putLong(TIME_LEFT, timeLeftMills);
        editor.putBoolean(IS_TIMER_RUNNING, isRunning);
        editor.putLong(END_TIME, endTime);
        editor.putLong(CUSTOM_TIME, timeInMills);
        editor.putInt(SPINNER_PROGRESS, progress);
        editor.putString(DISPLAY_SPEED_PERCENTAGE, timerPercentage);
        editor.putBoolean(SPEED_LESS_THAN_100, slowThan100Percent);
        editor.putInt(SPEED_PERCENTAGE, speedPercentage);
        editor.putLong(ACTUAL_REMAINING_TIME, actualRemainingTime);
        editor.putLong(ACTUAL_TIME_INTERVAL, actualCountDownInterval);
        editor.apply();
    }

    private void fetchInfoForNewTimer() {
        SharedPreferences prefs = Helpers.getSharedPreference(getApplicationContext());
        timeInMills = prefs.getLong(CUSTOM_TIME, MINS_TO_MILLS);
        timeLeftMills = prefs.getLong(TIME_LEFT, timeInMills);
        isRunning = prefs.getBoolean(IS_TIMER_RUNNING, false);
        progress = prefs.getInt(SPINNER_PROGRESS, 0);
        timerPercentage = prefs.getString(DISPLAY_SPEED_PERCENTAGE,"Time @100%");
        slowThan100Percent = prefs.getBoolean(SPEED_LESS_THAN_100,false);
        speedPercentage = prefs.getInt(SPEED_PERCENTAGE, 1);
        actualRemainingTime = prefs.getLong(ACTUAL_REMAINING_TIME, MINS_TO_MILLS);
        actualCountDownInterval = prefs.getLong(ACTUAL_TIME_INTERVAL,COUNTDOWN_INTERVAL);

        displayTimerPercentage.setText(timerPercentage);
        updateButtons();
        updateProgressBar(progress);
        refreshCountDownText();

        if (isRunning) {
            endTime = prefs.getLong(END_TIME, 0);
            long currentTime = System.currentTimeMillis();
            timeLeftMills = (endTime - currentTime) / speedPercentage;
            if (!slowThan100Percent) {
                timeLeftMills = (endTime - currentTime) * speedPercentage;
            }
            actualRemainingTime = timeLeftMills / speedPercentage;
            if (slowThan100Percent) {
                actualRemainingTime = timeLeftMills * speedPercentage;
            }
            if (actualRemainingTime <= 0) {
                timeLeftMills = 0;
                isRunning = false;
                refreshCountDownText();
                updateButtons();
            } else {
                startTimer(actualCountDownInterval,actualRemainingTime);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveInfoForOldTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchInfoForNewTimer();
    }
}
