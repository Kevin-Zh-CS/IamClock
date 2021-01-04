package me.jfenn.alarmio.data.preference;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.dialogs.AboutDialog;

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
            AboutDialog dialog = new AboutDialog();
            dialog.show(fragment_manager, null);
        }
    }
}

