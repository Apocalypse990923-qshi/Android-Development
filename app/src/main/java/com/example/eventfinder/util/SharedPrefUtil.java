package com.example.eventfinder.util;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    private static final String PREFS_NAME = "Favorites";

    public static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}