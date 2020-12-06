package com.iamclock.iamclockapp.Fragments.Clock;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Clock  {

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
}
