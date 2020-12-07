package com.iamclock.iamclockapp.Fragments.Clock;

public final class Clock {

    // === data ===
    private long time;
    private String label;
    private boolean[] repeat;
    private boolean enable;

    public Clock(long time, String label, boolean[] repeat, boolean enable) {
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
}
