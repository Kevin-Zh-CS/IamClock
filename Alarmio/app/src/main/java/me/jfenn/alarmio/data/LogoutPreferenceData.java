package me.jfenn.alarmio.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.data.preference.CustomPreferenceData;

public class LogoutPreferenceData extends CustomPreferenceData {
    public LogoutPreferenceData() {
        super(R.string.logout);
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @Override
    public void onClick(@NotNull ViewHolder holder) {
        SharedPreferences sharedPreferences = holder.getContext().getSharedPreferences("USER_INFO", Context.MODE_MULTI_PROCESS);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_NAME");
        editor.remove("ENCRYPT_PASSWORD");
        editor.apply();
        Toast.makeText(holder.getContext(), R.string.logout_done, Toast.LENGTH_SHORT).show();

    }
}
