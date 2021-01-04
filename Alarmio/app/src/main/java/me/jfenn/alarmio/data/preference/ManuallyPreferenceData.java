package me.jfenn.alarmio.data.preference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.jfenn.alarmio.R;

public class ManuallyPreferenceData extends CustomPreferenceData {
    public ManuallyPreferenceData() {
        super(R.string.title_manually);
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @Override
    public void onClick(@NotNull ViewHolder holder) {
        holder.getAlarmio().getHealthReport().Report();
    }
}
