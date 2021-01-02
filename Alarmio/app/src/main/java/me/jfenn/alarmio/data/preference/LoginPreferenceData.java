package me.jfenn.alarmio.data.preference;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.jfenn.alarmio.data.PreferenceData;

// TODO zhang: login button
public class LoginPreferenceData extends CustomPreferenceData {

    public LoginPreferenceData(PreferenceData preference, int name) {
        super(name);
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @Override
    public void onClick(@NotNull ViewHolder holder) {
        Log.d("TODO", "onClick:");
    }
}
