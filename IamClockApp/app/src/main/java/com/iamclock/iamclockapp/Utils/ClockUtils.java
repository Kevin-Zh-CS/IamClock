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

    public static String GetReadableDays(SparseBooleanArray days) {

        // test if all days are activated
        boolean workday_flag = true;
        for (int i = 1; i <= days.size() - 2; i++) {
            if (!days.valueAt(i)) {
                workday_flag = false;
                break;
            }
        }

        // test if all days are activated
        boolean weekend_flag = true;
        for (int i = days.size() - 1; i <= days.size(); i++) {
            if (!days.valueAt(i)) {
                weekend_flag = false;
                break;
            }
        }

        if (workday_flag && weekend_flag)
            return "DAILY";
        else if (workday_flag)
            return "WORKDAYS";
        else if (weekend_flag)
            return "WEEKEND";

        StringBuffer sb = new StringBuffer();

        sb.append(days.get(Clock.MON) ? "MON " : "");
        sb.append(days.get(Clock.TUES) ? "TUES " : "");
        sb.append(days.get(Clock.WED) ? "WED " : "");
        sb.append(days.get(Clock.THURS) ? "THURS " : "");
        sb.append(days.get(Clock.FRI) ? "FRI " : "");
        sb.append(days.get(Clock.SAT) ? "SAT " : "");
        sb.append(days.get(Clock.SUN) ? "SUN " : "");

        sb.deleteCharAt(sb.length());
        return sb.toString();
    }
}
