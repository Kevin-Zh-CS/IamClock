package com.iamclock.iamclockapp.Fragments.Clock;

public final class Clock {

    // === data ===
    private int hour;
    private int minute;
    private long time;
    private String label;
    private boolean[] repeat;
    private boolean enable;

    public Clock(int hour, int minute, long time, String label, boolean[] repeat, boolean enable) {
        this.minute = minute;
        this.hour = hour;
        this.time = time;
        this.label = label;
        this.repeat = repeat.clone();
        this.enable = enable;
    }

    public long GetTime() {
        return time;
    }

    public String GetLabel() {
        return label;
    }

    public boolean[] GetDays() {
        return repeat;
    }

    public boolean GetEnable() {
        return enable;
    }

    public void SetEnable(boolean enable) {
        this.enable = enable;
    }

    public int GetHour() {
        return hour;
    }

    public int GetMinute() {
        return minute;
    }
}
