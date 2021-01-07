package me.jfenn.alarmio.data.preference;

import android.annotation.SuppressLint;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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

    @SuppressLint("ShowToast")
    @Override
    public void onClick(@NotNull ViewHolder holder) {
        String ret = holder.getAlarmio().getHealthReport().Report(holder.getContext());
        Toast.makeText(holder.getContext(), ret, Toast.LENGTH_SHORT).show();
    }
}
