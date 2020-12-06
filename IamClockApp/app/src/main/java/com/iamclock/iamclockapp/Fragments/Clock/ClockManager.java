package com.iamclock.iamclockapp.Fragments.Clock;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClockManager {

    public static ArrayList<Clock> clock_list;

    public static void SaveClockSharedPreferences(Context context) {
        SharedPreferences clock_shared_preferences = context.getSharedPreferences("CLOCK_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = clock_shared_preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(clock_list);
        prefsEditor.putString("clock_list", json);
        prefsEditor.apply();
    }

    public static ArrayList<Clock> LoadClockSharedPreferences(Context context) {
        ArrayList<Clock> clock_list;
        SharedPreferences mPrefs = context.getSharedPreferences("CLOCK_PREFERENCES", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            clock_list = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Clock>>() {
            }.getType();
            clock_list = gson.fromJson(json, type);
        }
        return clock_list;
    }
}
