package me.jfenn.alarmio.data.preference;

import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.DialogBehavior;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.dialogs.AboutDialog;
import me.jfenn.alarmio.dialogs.LoginDialog;

public final class AboutPreferenceData extends CustomPreferenceData {

    public String getValueName(ViewHolder holder) {
        return null;
    }

    public AboutPreferenceData() {
        super(R.string.title_about);
    }

    // TODO add about window
    public void onClick(@NotNull ViewHolder holder) {
        FragmentManager fragment_manager = holder.getAlarmio().getFragmentManager();

        if (fragment_manager != null) {
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.show(fragment_manager, null);
        }
    }
}

