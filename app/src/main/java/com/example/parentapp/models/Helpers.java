package com.example.parentapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;

import com.example.parentapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
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

    //Adapted from https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
    public static String convertBitmapToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte [] byteArray = stream.toByteArray();
        String temp = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return temp;
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public static Bitmap convertStringToBitmap(String encodedString){
        try {
            byte [] byteArray = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
