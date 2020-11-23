package com.iamclock.iamclockapp.Fragments.Clock;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Clock implements Parcelable {

    // === data ===

    private static final long NO_ID = -1;

    private final long id;
    private long time;
    private String label;
    private SparseBooleanArray days;
    private boolean enable;

    // === day enumeration ===

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON, TUES, WED, THURS, FRI, SAT, SUN})
    @interface DAYS {}
    public static final int MON = 1;
    public static final int TUES = 2;
    public static final int WED = 3;
    public static final int THURS = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
    public static final int SUN = 7;

    // === constructors ===

    private Clock(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        label = in.readString();
        days = in.readSparseBooleanArray();
        enable = in.readByte() != 0;
    }

    public Clock() {
        this(NO_ID);
    }

    public Clock(long id) {
        this(id, System.currentTimeMillis());
    }

    public Clock(long id, long time, @DAYS int... days) {
        this(id, time, null, days);
    }

    public Clock(long id, long time, String label, @DAYS int... days) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.days = buildDaysArray(days);
    }


    public static final Creator<Clock> CREATOR = new Creator<Clock>() {
        public Clock createFromParcel(Parcel in) {
            return new Clock(in);
        }

        public Clock[] newArray(int size) {
            return new Clock[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(time);
        parcel.writeString(label);
        parcel.writeSparseBooleanArray(days);
        parcel.writeByte((byte) (enable ? 1 : 0));
    }

    public long GetID() {
        return id;
    }

    public void SetTime(long time) {
        this.time = time;
    }

    public long GetTime() {
        return time;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String GetLabel() {
        return label;
    }

    public void setDay(@DAYS int day, boolean isAlarmed) {
        days.append(day, isAlarmed);
    }

    public SparseBooleanArray GetDays() {
        return days;
    }

    public boolean getDay(@DAYS int day) {
        return days.get(day);
    }

    public void setIsEnabled(boolean isEnabled) {
        this.enable = isEnabled;
    }

    public boolean isEnable() {
        return enable;
    }

    public int notificationId() {
        final long id = GetID();
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", time=" + time +
                ", label='" + label + '\'' +
                ", allDays=" + days +
                ", isEnabled=" + enable +
                '}';
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + label.hashCode();
        for (int i = 0; i < days.size(); i++) {
            result = 31 * result + (days.valueAt(i) ? 1 : 0);
        }
        return result;
    }

    private static SparseBooleanArray buildDaysArray(@DAYS int... days) {
        final SparseBooleanArray array = buildBaseDaysArray();

        for (@DAYS int day : days) {
            array.append(day, true);
        }

        return array;
    }

    private static SparseBooleanArray buildBaseDaysArray() {
        final int num_days = 7;

        final SparseBooleanArray array = new SparseBooleanArray(num_days);

        array.put(MON, false);
        array.put(TUES, false);
        array.put(WED, false);
        array.put(THURS, false);
        array.put(FRI, false);
        array.put(SAT, false);
        array.put(SUN, false);

        return array;
    }
}
