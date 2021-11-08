package com.example.parentapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import static com.example.parentapp.TimerActivity.SEND_NOTIFICATION_ID;
/**
 * Adding sound and vibration along with notification when the timer ends:
 * https://stackoverflow.com/questions/31398381/how-to-add-custom-notification-sound-in-android
 */

public class NotificationReceiver extends BroadcastReceiver {
    public final static String NOTIFICATION_ID = "timerEnd";
    public final static String CHANNEL_ID = "channelID";
    public final static String SEND_CONTENT = "Calm down time is over!";
    public final static String SEND_TITLE = "Time is up! ";
    long[] pattern = {0, 100, 1000, 200, 2000};
    int passID;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotification = intent.getParcelableExtra(NOTIFICATION_ID);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.notification_music);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel sendNotification = new NotificationChannel(SEND_NOTIFICATION_ID,SEND_TITLE,NotificationManager.IMPORTANCE_HIGH);
            sendNotification.setDescription(SEND_CONTENT);
            sendNotification.enableVibration(true);
            sendNotification.setVibrationPattern(pattern);
            sendNotification.setSound(sound,attributes);
            notificationManager.createNotificationChannel(sendNotification);
        }
        passID = intent.getIntExtra(CHANNEL_ID,1);
        notificationManager.notify(passID,myNotification);
    }
}
