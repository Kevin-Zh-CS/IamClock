package me.jfenn.alarmio.data.preference;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import me.jfenn.alarmio.R;

public class ManuallyPreferenceData extends CustomPreferenceData {

    private FragmentActivity activity;

    public ManuallyPreferenceData(FragmentActivity activity) {
        super(R.string.title_manually);
        this.activity = activity;
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(@NotNull ViewHolder holder) {
        holder.getAlarmio().getHealthReport().Report(holder.getAlarmio(), activity);

    }
}
