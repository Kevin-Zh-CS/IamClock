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
    private static final String preference_key = "clock_list";

    public static void SaveClockSharedPreferences(Context context) {
        SharedPreferences clock_shared_preferences = context.getSharedPreferences("CLOCK_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor preference_editor = clock_shared_preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(clock_list);
        preference_editor.putString(preference_key, json);
        preference_editor.apply();
    }

    public static ArrayList<Clock> LoadClockSharedPreferences(Context context) {
        ArrayList<Clock> clock_list;
        SharedPreferences my_preference = context.getSharedPreferences("CLOCK_PREFERENCES", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = my_preference.getString(preference_key, "");
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
