package me.jfenn.alarmio.data.preference;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.jfenn.alarmio.data.HealthReportData;
import me.jfenn.alarmio.dialogs.HealthConfigDialog;

public class HealthConfigPreferenceData extends CustomPreferenceData {

    private HealthReportData health_report;

    public HealthConfigPreferenceData(HealthReportData health_report, int title) {
        super(title);
        this.health_report = health_report;
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @Override
    public void onClick(@NotNull ViewHolder holder) {
        FragmentManager fragmentManager = holder.getAlarmio().getFragmentManager();

        if (fragmentManager != null) {
            HealthConfigDialog dialog = new HealthConfigDialog();
            dialog.show(fragmentManager, null);
        }
    }
}