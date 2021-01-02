package me.jfenn.alarmio.data.preference;

import me.jfenn.alarmio.R;

public final class AboutPreferenceData extends CustomPreferenceData {

    public String getValueName(ViewHolder holder) {
        return null;
    }

    public void onClick(ViewHolder holder) {
        System.out.println("Hello World");
    }

    public AboutPreferenceData() {
        super(R.string.title_about);
    }
}

