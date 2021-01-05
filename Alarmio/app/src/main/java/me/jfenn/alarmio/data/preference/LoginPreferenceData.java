package me.jfenn.alarmio.data.preference;

import android.annotation.SuppressLint;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.dialogs.LoginDialog;

public class LoginPreferenceData extends CustomPreferenceData {

    public LoginPreferenceData(PreferenceData preference, int name) {
        super(name);
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(@NotNull ViewHolder holder) {
        FragmentManager fragmentManager = Objects.requireNonNull(holder.getAlarmio()).getFragmentManager();

        if (fragmentManager != null) {
            LoginDialog loginDialog = new LoginDialog(fragmentManager);
            loginDialog.show(fragmentManager, "LOGINDIALOG");
        }
    }
}
