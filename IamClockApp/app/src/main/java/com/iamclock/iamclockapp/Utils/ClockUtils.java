package com.iamclock.iamclockapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.SparseBooleanArray;

import androidx.core.app.ActivityCompat;

import com.iamclock.iamclockapp.Fragments.Clock.Clock;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ClockUtils {
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };

    private ClockUtils() {
        throw new AssertionError();
    }

    public static String GetReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }


    public static String GetReadableDays(boolean[] days) {


        if (CommonUtils.CheckBooleanArrayAll(days, true)) {
            return "DAILY";
        } else if (CommonUtils.CheckBooleanArrayAll(days, false)) {
            return "ONCE";
        } else if (days[0]
                && !days[1]
                && !days[2]
                && !days[3]
                && !days[4]
                && !days[5]
                && days[6]) {
            return "WEEKEND";
        } else if (!days[0]
                && days[1]
                && days[2]
                && days[3]
                && days[4]
                && days[5]
                && !days[6]) {
            return "DAILY";
        } else {
            return CommonUtils.GenerateRepeatString(days);
        }
    }
}
