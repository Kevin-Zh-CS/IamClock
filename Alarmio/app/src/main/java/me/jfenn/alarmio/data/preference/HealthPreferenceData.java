package me.jfenn.alarmio.data.preference;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.dialogs.HealthSubmitDialog;

public class HealthPreferenceData extends CustomPreferenceData {

    public HealthPreferenceData(PreferenceData preference, int time) {
        super(time);

    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @Override
    public void onClick(@NotNull ViewHolder holder) {
        FragmentManager fragment_manager = holder.getAlarmio().getFragmentManager();

        if (fragment_manager != null) {
            HealthSubmitDialog dialog = new HealthSubmitDialog();
            dialog.show(fragment_manager, (String) null);
        }
    }

    @NotNull
    @Override
    public CustomPreferenceData.ViewHolder getViewHolder(@NotNull LayoutInflater inflater, @NotNull ViewGroup parent) {
        return super.getViewHolder(inflater, parent);

    }
}
