package com.example.parentapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.parentapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * The Helpers class is a non instantiable class that contains a list of common functions used through out the app
 */
public class Helpers {

    public static final Gson gson = new Gson();

    // Prevent Helpers from being instantiated.
    private Helpers() {
    }

    public static boolean isStringNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static SharedPreferences getSharedPreference(Context context) {
        String sharedPrefKey = context.getResources().getString(R.string.shared_pref_key);

        return context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getSharedPrefEditor(Context context) {
        SharedPreferences sharedPref = Helpers.getSharedPreference(context);

        return sharedPref.edit();
    }

    public static void saveObjectToSharedPreference(Context context, String objectKey, Object object) {
        if (object == null) {
            throw new NullPointerException("Null object encountered at Helper -> saveObjectToSharedPreference.");
        }

        SharedPreferences.Editor sharedPrefEditor = getSharedPrefEditor(context);
        sharedPrefEditor.putString(objectKey, gson.toJson(object));

        sharedPrefEditor.apply();
    }

    public static <T> Type getClassType(Class<T> type) {
        return TypeToken.get(type).getType();
    }

    public static <T> Type getListOfClassType(Class<T> type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    public static <T> T getObjectFromSharedPreference(Context context, String objectKey, Type type) {
        SharedPreferences sharedPref = getSharedPreference(context);

        if (!sharedPref.contains(objectKey)) {
            throw new IllegalArgumentException("Helper -> getObjectFromSharedPreference: " + objectKey + " does not exist within the shared preference.");
        }

        String deserializeObject = sharedPref.getString(objectKey, null);

        if (Helpers.isStringNullOrEmpty(deserializeObject)) {
            throw new NullPointerException("Null or empty string encountered at Helper -> getObjectFromSharedPreference.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Helper -> getObjectFromSharedPreference: Type must be specified.");
        }

        return gson.fromJson(deserializeObject, type);
    }

    public static MediaPlayer getMediaPlayer(Context ctx, int resourceID) {
        return MediaPlayer.create(ctx, resourceID);
    }
    
    public static void vibratePhone(Context ctx, int intervalInMillis) {
        Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(intervalInMillis, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
